package com.rebloom.app.ui.screens.tasks

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.repository.TaskRepository
import com.rebloom.app.domain.model.TaskDefinition
import com.rebloom.app.ui.common.UiState
import com.rebloom.app.ui.theme.ErrorLoading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.SharedPreferences
import android.content.Context
import com.rebloom.app.ui.screens.widget.MascotWidgetReceiver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val _totalTasks = MutableStateFlow(0)
    val totalTasks: StateFlow<Int> = _totalTasks.asStateFlow()
    private val repository = TaskRepository(app.applicationContext)
    private val prefs: SharedPreferences = app.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
    private val _uiState = MutableStateFlow<UiState<List<TaskDefinition>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<TaskDefinition>>> = _uiState.asStateFlow()
    private val _completedIds = MutableStateFlow<Set<String>>(emptySet())
    val completedIds: StateFlow<Set<String>> = _completedIds.asStateFlow()
    val completedCount: StateFlow<Int> = completedIds.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadCompletedTasks()
        loadTasks()
    }

    private fun loadCompletedTasks() {
        val saved = prefs.getStringSet("completed_ids", emptySet()) ?: emptySet()
        _completedIds.value = saved.toSet()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val tasks = repository.loadTaskDefinitions()
                _uiState.value = UiState.Success(tasks)
                _totalTasks.value = tasks.size
                prefs.edit().putInt("total_tasks", tasks.size).apply()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: ErrorLoading)
            }
        }
    }
    fun toggleTaskCompletion(task: TaskDefinition){
        val taskId = task.id ?: task.hashCode().toString()
        val current = _completedIds.value.toMutableSet()
        if (current.contains(taskId)) current.remove(taskId) else current.add(taskId)
        _completedIds.value = current
        prefs.edit().putStringSet("completed_ids", current).commit()
        updateWidget()
    }
    private fun updateWidget() {
        val context = getApplication<Application>().applicationContext
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MascotWidgetReceiver::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        for (appWidgetId in appWidgetIds) {
            MascotWidgetReceiver.updateWidget(context, appWidgetManager, appWidgetId)
        }
    }
}