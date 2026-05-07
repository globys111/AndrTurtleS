package com.rebloom.app.data.repository

import android.content.Context
import android.net.Uri
import com.rebloom.app.RebloomApp
import com.rebloom.app.data.local.AppDatabase
import com.rebloom.app.data.local.UserPlantEntity
import com.rebloom.app.data.mapper.toDomain
import com.rebloom.app.data.mapper.toDto
import com.rebloom.app.data.mapper.toEntity
import com.rebloom.app.data.remote.StorageRemoteDataSource
import com.rebloom.app.data.remote.UserPlantRemoteDataSource
import com.rebloom.app.domain.model.Plant
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID

class UserPlantRepository(context: Context) {

    private val dao     = AppDatabase.getInstance(context).userPlantDao()
    private val remote  = UserPlantRemoteDataSource()
    private val storage = StorageRemoteDataSource(context)
    private val auth get() = RebloomApp.supabase.auth

    fun observePlants(): Flow<List<Plant>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun addPlant(
        nickname: String,
        notes: String,
        acquiredDate: String,
        plantId: String?,
        plantTypeName: String,
        photoUri: Uri?
    ): UserPlantEntity {
        val userId = auth.currentUserOrNull()?.id ?: error("Not logged in")
        val id = UUID.randomUUID().toString()
        val imageUrl = photoUri?.let { storage.uploadPhoto(it, id) }

        val entity = UserPlantEntity(
            id             = id,
            userId         = userId,
            plantId        = plantId,
            nickname       = nickname,
            plantTypeName  = plantTypeName,
            notes          = notes.ifEmpty { null },
            acquiredDate   = acquiredDate.ifEmpty { null },
            customImageUrl = imageUrl,
            room           = null,
            lastWateredAt  = null,
            createdAt      = Instant.now().toString(),
            isSynced       = false
        )
        dao.upsert(entity)
        return entity
    }

    suspend fun syncToRemote(entity: UserPlantEntity) {
        remote.upsert(entity.toDto())
        dao.upsert(entity.copy(isSynced = true))
    }

    suspend fun syncUnsynced() {
        dao.getUnsynced().forEach { syncToRemote(it) }
    }

    suspend fun deletePlant(plant: Plant) {
        dao.delete(plant.id)
        try {
            remote.delete(plant.id)
            if (plant.imageUrl != null) storage.deletePhoto(plant.id)
        } catch (_: Exception) {}
    }

    suspend fun syncFromRemote() {
        val userId = auth.currentUserOrNull()?.id ?: return
        val dtos = remote.fetchAll(userId)
        dao.upsertAll(dtos.map { it.toEntity() })
    }
}
