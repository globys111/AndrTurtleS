package com.rebloom.app.ui.screens.auth

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.R
import com.rebloom.app.RebloomApp
import com.rebloom.app.data.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isInitializing: Boolean = true,
    val isRegistrationPending: Boolean = false,
    val isPasswordRecovery: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    private val repository = AuthRepository()

    fun checkSession() {
        viewModelScope.launch {
            try {
                // Wait until Supabase finishes loading the session from storage.
                // SessionStatus.LoadingFromStorage is the initial state; we wait for anything else.
                val status = RebloomApp.supabase.auth.sessionStatus
                    .first { it::class.simpleName != "LoadingFromStorage" }
                val isLoggedIn = status::class.simpleName == "Authenticated"
                _uiState.value = _uiState.value.copy(isLoggedIn = isLoggedIn, isInitializing = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoggedIn = false, isInitializing = false)
            }
        }
    }

    fun handleDeepLink(uri: Uri) {
        viewModelScope.launch {
            try {
                repository.handleDeepLink(uri)
                val fragment = uri.fragment ?: ""
                when {
                    "type=signup" in fragment || "type=email_change" in fragment ->
                        _uiState.value = _uiState.value.copy(
                            isLoggedIn = true, isInitializing = false,
                            successMessage = "Email подтверждён! Добро пожаловать."
                        )
                    "type=recovery" in fragment ->
                        _uiState.value = _uiState.value.copy(
                            isPasswordRecovery = true, isInitializing = false
                        )
                    else -> _uiState.value = _uiState.value.copy(isInitializing = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isInitializing = false,
                    errorMessage = "Ошибка обработки ссылки: ${e.message}"
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when {
                email.isEmpty() -> err(R.string.error_email_empty)
                password.isEmpty() -> err(R.string.error_password_empty)
                else -> try {
                    repository.login(email, password)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, isLoggedIn = true,
                        successMessage = str(R.string.success_login)
                    )
                } catch (e: Exception) { err(e.message ?: str(R.string.error_login_failed)) }
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when {
                email.isEmpty() -> err(R.string.error_email_empty)
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> err(R.string.error_email_invalid)
                password.isEmpty() -> err(R.string.error_password_empty)
                password.length < 6 -> err(R.string.error_password_too_short)
                password != confirmPassword -> err(R.string.error_password_mismatch)
                else -> try {
                    repository.register(email, password)
                    if (repository.hasActiveSession()) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false, isLoggedIn = true,
                            successMessage = str(R.string.success_register)
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false, isRegistrationPending = true,
                            successMessage = "Письмо отправлено на $email. Нажмите ссылку для подтверждения."
                        )
                    }
                } catch (e: Exception) { err(e.message ?: str(R.string.error_login_failed)) }
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when {
                email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    err(R.string.forgot_password_error_email_invalid)
                else -> try {
                    repository.resetPassword(email)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, successMessage = str(R.string.forgot_password_success)
                    )
                } catch (e: Exception) { err(e.message ?: str(R.string.forgot_password_error_email_invalid)) }
            }
        }
    }

    fun updatePassword(password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when {
                password.length < 6 -> err(R.string.error_password_too_short)
                password != confirmPassword -> err(R.string.error_password_mismatch)
                else -> try {
                    repository.updatePassword(password)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, isLoggedIn = true, successMessage = "Пароль успешно изменён"
                    )
                } catch (e: Exception) { err(e.message ?: "Ошибка смены пароля") }
            }
        }
    }

    fun clearRegistrationPending() {
        _uiState.value = _uiState.value.copy(isRegistrationPending = false, successMessage = null)
    }
    fun clearPasswordRecovery() { _uiState.value = _uiState.value.copy(isPasswordRecovery = false) }
    fun resetState() { _uiState.value = AuthUiState(isInitializing = false) }
    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }
    fun clearSuccess() { _uiState.value = _uiState.value.copy(successMessage = null) }

    private fun str(id: Int) = getApplication<Application>().getString(id)
    private fun err(msg: String) { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = msg) }
    private fun err(id: Int) = err(str(id))
}
