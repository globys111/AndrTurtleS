package com.rebloom.app.ui.screens.home

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.local.TaskCompletionStore
import com.rebloom.app.data.repository.UserPlantRepository
import com.rebloom.app.domain.model.TaskOccurrence
import com.rebloom.app.domain.usecase.TaskScheduler
import com.rebloom.app.ui.common.UiState
import com.rebloom.app.ui.screens.widget.MascotWidgetReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val plantRepo = UserPlantRepository(app.applicationContext)

    private val _state = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val state: StateFlow<UiState<HomeData>> = _state

    val completedIds: StateFlow<Set<String>> = TaskCompletionStore.completedIds

    init {
        viewModelScope.launch { load() }
        viewModelScope.launch {
            try {
                plantRepo.syncFromRemote()
                val current = _state.value
                if (current is UiState.Success) {
                    val plants = plantRepo.observePlants().first()
                    _state.value = UiState.Success(current.data.copy(plants = plants))
                }
            } catch (_: Exception) {}
        }
    }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            try {
                val plants = plantRepo.observePlants().first()
                val today = LocalDate.now()

                val wateringDefs = TaskScheduler.generateWateringDefinitions(plants, today)
                val allOccurrences = TaskScheduler.generateOccurrences(
                    defs = wateringDefs,
                    today = today
                )

                _state.value = UiState.Success(
                    HomeData(
                        plants = plants,
                        allOccurrences = allOccurrences,
                        selectedDate = today,
                        today = today
                    )
                )
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun selectDate(date: LocalDate) {
        val current = _state.value
        if (current is UiState.Success) {
            _state.value = UiState.Success(current.data.copy(selectedDate = date))
        }
    }

    fun toggleTaskCompletion(task: TaskOccurrence) {
        TaskCompletionStore.toggle(task.completionKey)
        updateTodayCounts()
        updateWidget()
    }

    private fun updateTodayCounts() {
        val s = _state.value as? UiState.Success ?: return
        val today = LocalDate.now()
        val todayTasks = TaskScheduler.tasksForDate(s.data.allOccurrences, today)
        val completed = todayTasks.count { TaskCompletionStore.completedIds.value.contains(it.completionKey) }
        TaskCompletionStore.saveTodayCounts(completed, todayTasks.size)
    }

    private fun updateWidget() {
        val ctx = getApplication<Application>().applicationContext
        val mgr = AppWidgetManager.getInstance(ctx)
        val ids = mgr.getAppWidgetIds(ComponentName(ctx, MascotWidgetReceiver::class.java))
        ids.forEach { MascotWidgetReceiver.updateWidget(ctx, mgr, it) }
    }
}
