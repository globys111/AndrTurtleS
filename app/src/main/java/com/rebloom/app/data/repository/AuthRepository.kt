package com.rebloom.app.data.repository

import android.net.Uri
import android.util.Log
import com.rebloom.app.RebloomApp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

private const val TAG = "AUTH"

class AuthRepository {
    private val auth get() = RebloomApp.supabase.auth
    private val db get() = RebloomApp.supabase.postgrest


    suspend fun login(email: String, password: String) {
        Log.d(TAG, "login → email=$email")
        try {
            auth.signInWith(Email) { this.email = email; this.password = password }
            auth.currentUserOrNull()?.id?.let { ensureProfileExists(it) }
            Log.d(TAG, "login ✓")
        } catch (e: Exception) {
            Log.e(TAG, "login ✗ ${e::class.simpleName}: ${e.message}")
            throw e
        }
    }

    suspend fun register(email: String, password: String) {
        Log.d(TAG, "register → email=$email")
        try {
            auth.signUpWith(Email) { this.email = email; this.password = password }
            val session = auth.currentSessionOrNull()
            Log.d(TAG, "register ✓ sessionActive=${session != null}")
            session?.user?.id?.let { uid ->
                ensureProfileExists(uid)
            }
        } catch (e: Exception) {
            Log.e(TAG, "register ✗ ${e::class.simpleName}: ${e.message}")
            throw e
        }
    }

    suspend fun resetPassword(email: String) {
        Log.d(TAG, "resetPassword → email=$email")
        try {
            auth.resetPasswordForEmail(email)
            Log.d(TAG, "resetPassword ✓ email sent")
        } catch (e: Exception) {
            Log.e(TAG, "resetPassword ✗ ${e::class.simpleName}: ${e.message}")
            throw e
        }
    }

    suspend fun updatePassword(newPassword: String) {
        Log.d(TAG, "updatePassword →")
        try {
            auth.updateUser { password = newPassword }
            Log.d(TAG, "updatePassword ✓")
        } catch (e: Exception) {
            Log.e(TAG, "updatePassword ✗ ${e::class.simpleName}: ${e.message}")
            throw e
        }
    }

    suspend fun signOut() {
        Log.d(TAG, "signOut →")
        try {
            auth.signOut()
            Log.d(TAG, "signOut ✓")
        } catch (e: Exception) {
            Log.e(TAG, "signOut ✗ ${e.message}")
            throw e
        }
    }

    suspend fun deleteAccount() {
        Log.d(TAG, "deleteAccount →")
        try {
            db.rpc("delete_user")
            Log.d(TAG, "deleteAccount ✓")
        } catch (e: Exception) {
            Log.e(TAG, "deleteAccount ✗ ${e.message}")
            throw e
        }
    }

    suspend fun handleDeepLink(uri: Uri) {
        Log.d(TAG, "handleDeepLink → uri=$uri")
        val fragment = uri.fragment ?: return
        val params = fragment.split("&").mapNotNull {
            val parts = it.split("=", limit = 2)
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()

        val accessToken = params["access_token"] ?: return
        val refreshToken = params["refresh_token"] ?: ""
        try {
            auth.importAuthToken(accessToken, refreshToken)
            auth.currentUserOrNull()?.id?.let { ensureProfileExists(it) }
            Log.d(TAG, "handleDeepLink ✓")
        } catch (e: Exception) {
            Log.e(TAG, "handleDeepLink ✗ ${e.message}")
            throw e
        }
    }

    private suspend fun ensureProfileExists(uid: String) {
        try {
            db.rpc(
                function = "generate_default_profile",
                parameters = buildJsonObject { put("user_id", uid) }
            )
            Log.d(TAG, "ensureProfileExists ✓ uid=$uid")
        } catch (e: Exception) {
            Log.w(TAG, "ensureProfileExists ✗ ${e.message}")
        }
    }

    fun hasActiveSession(): Boolean {
        val active = auth.currentSessionOrNull() != null
        Log.d(TAG, "hasActiveSession=$active")
        return active
    }
}