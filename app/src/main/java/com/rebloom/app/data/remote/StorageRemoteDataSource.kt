package com.rebloom.app.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.rebloom.app.RebloomApp
import io.github.jan.supabase.storage.storage
import java.io.File

private const val TAG = "StorageDS"

class StorageRemoteDataSource(private val context: Context) {

    private val bucket get() = RebloomApp.supabase.storage["plant-images"]

    // Copies picked photo to internal storage immediately (no network needed).
    // Returns a "file://..." URI string that Coil can load directly.
    fun copyToLocal(uri: Uri, plantId: String): String? {
        return try {
            val dir = File(context.filesDir, "plant_photos").apply { mkdirs() }
            val file = File(dir, "$plantId.jpg")
            context.contentResolver.openInputStream(uri)?.use { it.copyTo(file.outputStream()) }
            "file://${file.absolutePath}"
        } catch (e: Exception) {
            Log.e(TAG, "copyToLocal: failed for plantId=$plantId", e)
            null
        }
    }

    fun deleteLocalPhoto(plantId: String) {
        try { File(context.filesDir, "plant_photos/$plantId.jpg").delete() } catch (_: Exception) {}
    }

    suspend fun uploadPhoto(uri: Uri, plantId: String): String? {
        Log.d(TAG, "uploadPhoto: plantId=$plantId")
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: return null
        Log.d(TAG, "uploadPhoto: read ${bytes.size} bytes, uploading")
        val path = "user_plants/$plantId.jpg"
        bucket.upload(path, bytes) { upsert = true }
        val url = bucket.publicUrl(path) + "?t=${System.currentTimeMillis()}"
        Log.d(TAG, "uploadPhoto: success url=$url")
        return url
    }

    suspend fun uploadFromLocalFile(plantId: String): String? {
        return try {
            val file = File(context.filesDir, "plant_photos/$plantId.jpg")
            if (!file.exists()) return null
            val bytes = file.readBytes()
            val path = "user_plants/$plantId.jpg"
            bucket.upload(path, bytes) { upsert = true }
            val url = bucket.publicUrl(path) + "?t=${System.currentTimeMillis()}"
            Log.d(TAG, "uploadFromLocalFile: success url=$url")
            url
        } catch (e: Exception) {
            Log.e(TAG, "uploadFromLocalFile: failed for $plantId", e)
            null
        }
    }

    suspend fun downloadToLocal(url: String, plantId: String): Boolean {
        return try {
            val dir = File(context.filesDir, "plant_photos").apply { mkdirs() }
            val file = File(dir, "$plantId.jpg")
            val connection = java.net.URL(url).openConnection()
            connection.connectTimeout = 10_000
            connection.readTimeout = 15_000
            val bytes = connection.getInputStream().use { it.readBytes() }
            file.writeBytes(bytes)
            Log.d(TAG, "downloadToLocal: saved ${bytes.size} bytes for $plantId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "downloadToLocal: failed for $plantId", e)
            false
        }
    }

    fun localFileUrl(plantId: String): String? {
        val file = File(context.filesDir, "plant_photos/$plantId.jpg")
        return if (file.exists()) "file://${file.absolutePath}" else null
    }

    suspend fun deletePhoto(plantId: String) {
        try {
            bucket.delete(listOf("user_plants/$plantId.jpg"))
        } catch (_: Exception) {}
    }
}
