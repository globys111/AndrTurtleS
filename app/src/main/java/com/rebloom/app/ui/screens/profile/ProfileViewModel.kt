// app/src/main/java/com/rebloom/app/ui/screens/profile/ProfileViewModel.kt
package com.rebloom.app.ui.screens.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.repository.AuthRepository
import com.rebloom.app.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val username: String = "",
    val email: String = "",
    val bio: String = "",
    val avatarUrl: String? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val isSignedOut: Boolean = false
)

class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val profileRepo = ProfileRepository()
    private val authRepo = AuthRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val profile = profileRepo.getProfile()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    username = profile.username ?: "",
                    email = profileRepo.currentEmail() ?: "",
                    bio = profile.bio ?: "",
                    avatarUrl = profile.avatar_url
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    email = profileRepo.currentEmail() ?: "",
                    errorMessage = e.message
                )
            }
        }
    }

    fun saveProfile(username: String, bio: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                profileRepo.updateProfile(username, bio)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    username = username,
                    bio = bio,
                    successMessage = "Профиль обновлён"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, errorMessage = "Ошибка: ${e.message}"
                )
            }
        }
    }

    fun uploadAndSaveAvatar(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val url = profileRepo.uploadAvatar(getApplication(), imageUri)
                profileRepo.updateAvatarUrl(url)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    avatarUrl = url,
                    successMessage = "Аватарка обновлена"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, errorMessage = "Ошибка загрузки: ${e.message}"
                )
            }
        }
    }

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                profileRepo.updateEmail(newEmail)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "На $newEmail отправлено письмо для подтверждения"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, errorMessage = "Ошибка: ${e.message}"
                )
            }
        }
    }

    fun updatePassword(newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            when {
                newPassword.length < 6 -> {
                    _uiState.value = _uiState.value.copy(errorMessage = "Минимум 6 символов")
                    return@launch
                }
                newPassword != confirmPassword -> {
                    _uiState.value = _uiState.value.copy(errorMessage = "Пароли не совпадают")
                    return@launch
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                profileRepo.updatePassword(newPassword)
                _uiState.value = _uiState.value.copy(
                    isLoading = false, successMessage = "Пароль изменён"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, errorMessage = "Ошибка: ${e.message}"
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                authRepo.signOut()
                _uiState.value = _uiState.value.copy(isLoading = false, isSignedOut = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, errorMessage = "Ошибка выхода: ${e.message}"
                )
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                authRepo.deleteAccount()
                _uiState.value = _uiState.value.copy(isLoading = false, isSignedOut = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, errorMessage = "Ошибка удаления: ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }
}