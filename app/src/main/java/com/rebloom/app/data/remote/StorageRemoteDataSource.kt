package com.rebloom.app.data.remote

import android.content.Context
import android.net.Uri
import com.rebloom.app.RebloomApp
import io.github.jan.supabase.storage.storage

class StorageRemoteDataSource(private val context: Context) {

    private val bucket get() = RebloomApp.supabase.storage["plant-images"]

    suspend fun uploadPhoto(uri: Uri, plantId: String): String? {
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: return null
        val path = "user_plants/$plantId.jpg"
        bucket.upload(path, bytes) { upsert = true }
        return bucket.publicUrl(path)
    }

    suspend fun deletePhoto(plantId: String) {
        try {
            bucket.delete(listOf("user_plants/$plantId.jpg"))
        } catch (_: Exception) {}
    }
}
