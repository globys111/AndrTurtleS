package com.rebloom.app.ui.screens.tasks

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rebloom.app.data.local.TaskCompletionStore
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

    private val plantsFlow = plantRepo.observePlants()

    private val _taskOccurrences = MutableStateFlow<List<TaskOccurrence>>(emptyList())
    val taskOccurrences: StateFlow<List<TaskOccurrence>> = _taskOccurrences.asStateFlow()

    val completedIds: StateFlow<Set<String>> = TaskCompletionStore.completedIds

    private val _filter = MutableStateFlow(TaskFilter.LIST)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    val filteredTasks: StateFlow<List<TaskOccurrence>> = combine(
        _taskOccurrences, TaskCompletionStore.completedIds, _filter
    ) { tasks, completed, filter ->
        val today = LocalDate.now()
        tasks.filter { task ->
            val isCompleted = completed.contains(task.completionKey)
            when (filter) {
                TaskFilter.LIST -> task.date == today
                TaskFilter.OVERDUE -> task.date.isBefore(today) && !isCompleted
                TaskFilter.ALL -> true
            }
        }.sortedWith(compareBy({ it.date }, { it.time }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            plantsFlow.collect { plants ->
                generateTasks(plants)
            }
        }
    }

    private fun generateTasks(plants: List<Plant>) {
        val today = LocalDate.now()
        val wateringDefs = TaskScheduler.generateWateringDefinitions(plants, today)
        val occurrences = TaskScheduler.generateOccurrences(wateringDefs, today)
        _taskOccurrences.value = occurrences
        val todayTasks = occurrences.filter { it.date == today }
        val todayCompleted = todayTasks.count { task ->
            TaskCompletionStore.completedIds.value.contains(task.completionKey)
        }
        TaskCompletionStore.saveTodayCounts(todayCompleted, todayTasks.size)
        updateWidget()
    }

    fun toggleTaskCompletion(task: TaskOccurrence) {
        TaskCompletionStore.toggle(task.completionKey)
        val today = LocalDate.now()
        val todayTasks = _taskOccurrences.value.filter { it.date == today }
        val todayCompleted = todayTasks.count { TaskCompletionStore.completedIds.value.contains(it.completionKey) }
        TaskCompletionStore.saveTodayCounts(todayCompleted, todayTasks.size)
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

    fun setFilter(newFilter: TaskFilter) { _filter.value = newFilter }

    enum class TaskFilter { LIST, OVERDUE, ALL }
}
