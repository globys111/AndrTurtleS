package com.rebloom.app.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.rebloom.app.RebloomApp
import com.rebloom.app.data.local.AppDatabase
import com.rebloom.app.data.local.UserPlantEntity
import com.rebloom.app.data.mapper.toDomain
import com.rebloom.app.data.mapper.toDto
import com.rebloom.app.data.mapper.toEntity
import com.rebloom.app.data.remote.StorageRemoteDataSource
import com.rebloom.app.data.remote.UserPlantRemoteDataSource
import com.rebloom.app.data.remote.SyncPlantsWorker
import com.rebloom.app.domain.model.Plant
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID

private const val TAG = "PlantRepo"

class UserPlantRepository(private val context: Context) {

    private val dao     = AppDatabase.getInstance(context).userPlantDao()
    private val remote  = UserPlantRemoteDataSource()
    private val storage = StorageRemoteDataSource(context)
    private val auth get() = RebloomApp.supabase.auth

    fun observePlants(): Flow<List<Plant>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observePlantById(id: String): Flow<Plant?> =
        dao.observeById(id).map { it?.toDomain() }

    suspend fun addPlant(
        nickname: String,
        notes: String,
        acquiredDate: String,
        plantId: String?,
        plantTypeName: String,
        wateringLevel: Int
    ): UserPlantEntity {
        val userId = auth.currentUserOrNull()?.id ?: error("Not logged in")
        val id = UUID.randomUUID().toString()
        val entity = UserPlantEntity(
            id             = id,
            userId         = userId,
            plantId        = plantId,
            nickname       = nickname,
            plantTypeName  = plantTypeName,
            notes          = notes.ifEmpty { null },
            acquiredDate   = acquiredDate.ifEmpty { null },
            customImageUrl = null,
            room           = null,
            lastWateredAt  = null,
            createdAt      = Instant.now().toString(),
            isSynced       = false,
            wateringLevel  = wateringLevel
        )
        dao.upsert(entity)
        SyncPlantsWorker.enqueue(context)
        return entity
    }

    suspend fun updatePlant(
        plantId: String,
        nickname: String,
        notes: String,
        acquiredDate: String,
        plantTypeName: String,
        wateringLevel: Int
    ): UserPlantEntity {
        Log.d(TAG, "updatePlant: looking up id=$plantId")
        val existing = dao.getById(plantId) ?: error("Plant not found: $plantId")
        Log.d(TAG, "updatePlant: found existing, customImageUrl=${existing.customImageUrl}")
        val updated = existing.copy(
            nickname      = nickname,
            notes         = notes.ifEmpty { null },
            acquiredDate  = acquiredDate.ifEmpty { null },
            plantTypeName = plantTypeName,
            isSynced      = false,
            wateringLevel  = wateringLevel
        )
        dao.upsert(updated)
        SyncPlantsWorker.enqueue(context)
        Log.d(TAG, "updatePlant: saved to Room, returning entity id=${updated.id}")
        return updated
    }

    suspend fun uploadPhotoAndSync(entity: UserPlantEntity, newPhotoUri: Uri?) {
        Log.d(TAG, "uploadPhotoAndSync: id=${entity.id} newPhotoUri=$newPhotoUri")

        if (newPhotoUri == null) {
            val e = entity.copy(isSynced = false)
            dao.upsert(e)
            try { syncToRemote(e) } catch (e2: Exception) {
                if (e2 is kotlinx.coroutines.CancellationException) throw e2
                Log.e(TAG, "uploadPhotoAndSync: sync FAILED (no photo change)", e2)
            }
            return
        }

        // 1. Copy to internal storage immediately → save to Room so photo shows without network
        val localUrl = storage.copyToLocal(newPhotoUri, entity.id)
        if (localUrl != null) {
            Log.d(TAG, "uploadPhotoAndSync: local copy saved $localUrl")
            dao.upsert(entity.copy(customImageUrl = localUrl, isSynced = false))
        }

        // 2. Upload to Supabase in background; failure is safe — local copy already in Room
        try {
            val supabaseUrl = storage.uploadPhoto(newPhotoUri, entity.id)
            Log.d(TAG, "uploadPhotoAndSync: supabase url=$supabaseUrl")
            if (supabaseUrl != null) {
                val withRemote = entity.copy(customImageUrl = supabaseUrl, isSynced = false)
                dao.upsert(withRemote)
                if (localUrl != null) storage.deleteLocalPhoto(entity.id)
                syncToRemote(withRemote)
                Log.d(TAG, "uploadPhotoAndSync: remote sync done")
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Log.e(TAG, "uploadPhotoAndSync: supabase upload failed, local copy preserved", e)
        }
    }

    suspend fun syncToRemote(entity: UserPlantEntity) {
        remote.upsert(entity.toDto())
        dao.upsert(entity.copy(isSynced = true))
    }

    suspend fun syncUnsyncedWithPhotos(): Boolean {
        var allSynced = true
        dao.getUnsynced().forEach { entity ->
            try {
                val toSync = if (entity.customImageUrl?.startsWith("file://") == true) {
                    val supabaseUrl = storage.uploadFromLocalFile(entity.id)
                    if (supabaseUrl != null) {
                        val updated = entity.copy(customImageUrl = supabaseUrl, isSynced = false)
                        dao.upsert(updated)
                        storage.deleteLocalPhoto(entity.id)
                        updated
                    } else {
                        allSynced = false
                        return@forEach
                    }
                } else {
                    entity
                }
                syncToRemote(toSync)
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                allSynced = false
                Log.e(TAG, "syncUnsyncedWithPhotos: failed for ${entity.id}", e)
            }
        }
        return allSynced
    }

    suspend fun deletePlant(plant: Plant) {
        dao.delete(plant.id)
        storage.deleteLocalPhoto(plant.id)
        try {
            remote.delete(plant.id)
            if (plant.imageUrl?.startsWith("http") == true) storage.deletePhoto(plant.id)
        } catch (_: Exception) {}
    }

    suspend fun syncFromRemote() {
        val userId = auth.currentUserOrNull()?.id ?: return
        val dtos = remote.fetchAll(userId)
        // Don't overwrite entities with unsynced local changes (e.g. pending photo uploads)
        val unsyncedIds = dao.getUnsynced().map { it.id }.toSet()
        val toUpsert = dtos.map { it.toEntity() }.filter { it.id !in unsyncedIds }
        dao.upsertAll(toUpsert)
    }
}
