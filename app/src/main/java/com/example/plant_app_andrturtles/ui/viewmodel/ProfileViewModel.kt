package com.example.plant_app_andrturtles.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plant_app_andrturtles.data.repository.MockRepository
import com.example.plant_app_andrturtles.domain.model.User
import com.example.plant_app_andrturtles.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MockRepository(app.applicationContext)

    private val _state = MutableStateFlow<UiState<User>>(UiState.Loading)
    val state: StateFlow<UiState<User>> = _state

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            try {
                _state.value = UiState.Success(repo.loadUser())
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
