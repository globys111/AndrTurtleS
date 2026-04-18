package com.rebloom.app.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val testEmail = "test@mail.com"
    private val testPassword = "123456"

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            kotlinx.coroutines.delay(1000)

            when {
                email.isEmpty() -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getApplication<Application>().getString(R.string.error_email_empty)
                    )
                }
                password.isEmpty() -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getApplication<Application>().getString(R.string.error_password_empty)
                    )
                }
                email == testEmail && password == testPassword -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        successMessage = getApplication<Application>().getString(R.string.success_login)
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getApplication<Application>().getString(R.string.error_login_failed) + "\n\n" +
                                getApplication<Application>().getString(R.string.error_test_data_hint)
                    )
                }
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            kotlinx.coroutines.delay(1000)

            when {
                email.isEmpty() -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getApplication<Application>().getString(R.string.error_email_empty)
                    )
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getApplication<Application>().getString(R.string.error_email_invalid)
                    )
                }
                password.isEmpty() -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getApplication<Application>().getString(R.string.error_password_empty)
                    )
                }
                password.length < 6 -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getApplication<Application>().getString(R.string.error_password_too_short)
                    )
                }
                password != confirmPassword -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getApplication<Application>().getString(R.string.error_password_mismatch)
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        successMessage = getApplication<Application>().getString(R.string.success_register)
                    )
                }
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            kotlinx.coroutines.delay(1000)

            if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = getApplication<Application>().getString(R.string.forgot_password_success)
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = getApplication<Application>().getString(R.string.forgot_password_error_email_invalid)
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}