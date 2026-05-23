// app/src/main/java/com/rebloom/app/data/repository/ProfileRepository.kt
package com.rebloom.app.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.rebloom.app.RebloomApp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.InputStream

private const val TAG = "PROFILE"

@Serializable
data class ProfileRow(
    val id: String,
    val username: String? = null,
    val avatar_url: String? = null,
    val bio: String? = null
)

class ProfileRepository {
    private val auth get() = RebloomApp.supabase.auth
    private val db get() = RebloomApp.supabase.postgrest
    private val storage get() = RebloomApp.supabase.storage

    fun currentUserId(): String? = auth.currentUserOrNull()?.id
    fun currentEmail(): String? = auth.currentUserOrNull()?.email

    suspend fun getProfile(): ProfileRow {
        val uid = currentUserId() ?: error("Not authenticated")
        return db["profiles"]
            .select(Columns.ALL) { filter { eq("id", uid) } }
            .decodeSingle()
    }

    suspend fun updateProfile(username: String, bio: String) {
        val uid = currentUserId() ?: error("Not authenticated")
        db["profiles"].update(
            buildJsonObject {
                put("username", username)
                put("bio", bio)
            }
        ) {
            filter { eq("id", uid) }
        }
        Log.d(TAG, "updateProfile ✓")
    }

    suspend fun uploadAvatar(context: Context, imageUri: Uri): String {
        val session = RebloomApp.supabase.auth.currentSessionOrNull()
            ?: error("Not authenticated")
        val uid = session.user?.id
            ?: error("Not authenticated")

        val bucket = storage["avatars"]

        val inputStream: InputStream = context.contentResolver.openInputStream(imageUri)
            ?: error("Cannot open image")
        val bytes = inputStream.readBytes()
        inputStream.close()

        val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
        val extension = when (mimeType) {
            "image/png"  -> "png"
            "image/webp" -> "webp"
            else         -> "jpg"
        }
        val path = "$uid/avatar.$extension"

        bucket.upload(path, bytes) { upsert = true }

        val publicUrl = bucket.publicUrl(path)
        Log.d(TAG, "uploadAvatar ✓ url=$publicUrl")
        return publicUrl
    }

    suspend fun updateAvatarUrl(avatarUrl: String) {
        val uid = currentUserId() ?: error("Not authenticated")
        db["profiles"].update(
            buildJsonObject { put("avatar_url", avatarUrl) }
        ) {
            filter { eq("id", uid) }
        }
        Log.d(TAG, "updateAvatarUrl ✓")
    }

    suspend fun updateEmail(newEmail: String) {
        auth.updateUser { email = newEmail }
        Log.d(TAG, "updateEmail ✓")
    }

    suspend fun updatePassword(newPassword: String) {
        auth.updateUser { password = newPassword }
        Log.d(TAG, "updatePassword ✓")
    }
}