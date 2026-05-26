package com.rebloom.app.ui.screens.tasks

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.repository.UserPlantRepository
import com.rebloom.app.domain.model.Plant
import com.rebloom.app.domain.model.TaskOccurrence
import com.rebloom.app.domain.usecase.TaskScheduler
import com.rebloom.app.ui.screens.widget.MascotWidgetReceiver
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val plantRepo = UserPlantRepository(app.applicationContext)
    private val prefs: SharedPreferences = app.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)

    private val plantsFlow = plantRepo.observePlants()

    private val _taskOccurrences = MutableStateFlow<List<TaskOccurrence>>(emptyList())
    val taskOccurrences: StateFlow<List<TaskOccurrence>> = _taskOccurrences.asStateFlow()

    private val _completedIds = MutableStateFlow<Set<String>>(emptySet())
    val completedIds: StateFlow<Set<String>> = _completedIds.asStateFlow()

    private val _filter = MutableStateFlow(TaskFilter.LIST)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    val filteredTasks: StateFlow<List<TaskOccurrence>> = combine(
        _taskOccurrences, _completedIds, _filter
    ) { tasks, completed, filter ->
        val today = LocalDate.now()
        tasks.filter { task ->
            val isCompleted = completed.contains(taskKey(task))
            when (filter) {
                TaskFilter.LIST -> task.date == today
                TaskFilter.OVERDUE -> task.date.isBefore(today) && !isCompleted
                TaskFilter.ALL -> true
            }
        }.sortedWith(compareBy({ it.date }, { it.time }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadCompletedTasks()
        viewModelScope.launch {
            plantsFlow.collect { plants ->
                generateTasks(plants)
            }
        }
    }

    private fun loadCompletedTasks() {
        val saved = prefs.getStringSet("completed_ids", emptySet()) ?: emptySet()
        _completedIds.value = saved.toSet()
    }

    private fun generateTasks(plants: List<Plant>) {
        val today = LocalDate.now()
        val wateringDefs = TaskScheduler.generateWateringDefinitions(plants, today)
        val occurrences = TaskScheduler.generateOccurrences(wateringDefs, today)
        _taskOccurrences.value = occurrences
        val todayTasks = occurrences.filter { it.date == today }
        val todayTotal = todayTasks.size
        val todayCompleted = todayTasks.count { task ->
            _completedIds.value.contains(taskKey(task))
        }

        prefs.edit()
            .putInt("today_total", todayTotal)
            .putInt("today_completed", todayCompleted)
            .apply()

        updateWidget()


    }

    fun toggleTaskCompletion(task: TaskOccurrence) {
        val key = taskKey(task)
        val current = _completedIds.value.toMutableSet()
        if (current.contains(key)) current.remove(key) else current.add(key)
        _completedIds.value = current
        prefs.edit().putStringSet("completed_ids", current).commit()
        val today = LocalDate.now()
        val todayTasks = _taskOccurrences.value.filter { it.date == today }
        val todayCompleted = todayTasks.count { current.contains(taskKey(it)) }
        prefs.edit().putInt("today_completed", todayCompleted).apply()

        updateWidget()
    }

    private fun updateWidget() {
        val context = getApplication<Application>().applicationContext
        val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(context)
        val componentName = android.content.ComponentName(context, MascotWidgetReceiver::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        for (appWidgetId in appWidgetIds) {
            MascotWidgetReceiver.updateWidget(context, appWidgetManager, appWidgetId)
        }
    }


    fun setFilter(newFilter: TaskFilter) { _filter.value = newFilter }

    private fun taskKey(task: TaskOccurrence) = "${task.definitionId}_${task.date}"

    enum class TaskFilter { LIST, OVERDUE, ALL }
}