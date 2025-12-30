package com.example.plant_app_andrturtles.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plant_app_andrturtles.data.repository.MockRepository
import com.example.plant_app_andrturtles.domain.model.Post
import com.example.plant_app_andrturtles.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommunityViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = MockRepository(app.applicationContext)

    private val _state = MutableStateFlow<UiState<List<Post>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Post>>> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val posts = repo.loadPosts()
                _state.value = UiState.Success(posts)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}