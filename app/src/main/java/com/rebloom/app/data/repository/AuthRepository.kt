package com.rebloom.app.data.repository

import android.net.Uri
import android.util.Log
import com.rebloom.app.RebloomApp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

private const val TAG = "AUTH"

class AuthRepository {
    private val auth get() = RebloomApp.supabase.auth

    suspend fun login(email: String, password: String) {
        Log.d(TAG, "login → email=$email")
        try {
            auth.signInWith(Email) { this.email = email; this.password = password }
            Log.d(TAG, "login ✓ user=${auth.currentUserOrNull()?.email}")
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
            Log.d(TAG, "register ✓ sessionActive=${session != null} user=${session?.user?.email}")
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

    suspend fun handleDeepLink(uri: Uri) {
        Log.d(TAG, "handleDeepLink → uri=$uri")
        val fragment = uri.fragment ?: run {
            Log.w(TAG, "handleDeepLink: no fragment in URI")
            return
        }
        val params = fragment.split("&").mapNotNull {
            val parts = it.split("=", limit = 2)
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()
        Log.d(TAG, "handleDeepLink params: type=${params["type"]} hasAccessToken=${params.containsKey("access_token")}")
        val accessToken = params["access_token"] ?: run {
            Log.w(TAG, "handleDeepLink: no access_token")
            return
        }
        val refreshToken = params["refresh_token"] ?: ""
        try {
            auth.importAuthToken(accessToken, refreshToken)
            Log.d(TAG, "handleDeepLink ✓ token imported, user=${auth.currentUserOrNull()?.email}")
        } catch (e: Exception) {
            Log.e(TAG, "handleDeepLink ✗ ${e::class.simpleName}: ${e.message}")
            throw e
        }
    }

    fun hasActiveSession(): Boolean {
        val active = auth.currentSessionOrNull() != null
        Log.d(TAG, "hasActiveSession=$active")
        return active
    }
}
