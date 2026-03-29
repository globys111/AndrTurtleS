package com.rebloom.app.ui.screens.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.repository.TaskRepository
import com.rebloom.app.domain.model.TaskDefinition
import com.rebloom.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = TaskRepository(app.applicationContext)

    private val _uiState = MutableStateFlow<UiState<List<TaskDefinition>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<TaskDefinition>>> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val tasks = repository.loadTaskDefinitions()
                _uiState.value = UiState.Success(tasks)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Ошибка загрузки задач")
            }
        }
    }
}