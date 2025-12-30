package com.example.plant_app_andrturtles.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plant_app_andrturtles.data.repository.MockRepository
import com.example.plant_app_andrturtles.domain.model.Article
import com.example.plant_app_andrturtles.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticlesViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = MockRepository(app.applicationContext)

    private val _state = MutableStateFlow<UiState<List<Article>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Article>>> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val articles = repo.loadArticles()
                _state.value = UiState.Success(articles)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}