package com.rebloom.app.data.notification

import android.content.Context
import android.content.SharedPreferences
import androidx.work.*
import com.rebloom.app.data.repository.TaskRepository
import com.rebloom.app.domain.model.TaskType
import java.util.concurrent.TimeUnit

class WateringCheck(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prefs: SharedPreferences = applicationContext
            .getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
        val completedIds = prefs.getStringSet("completed_ids", emptySet()) ?: emptySet()
        val repository = TaskRepository(applicationContext)

        return try {
            val tasks = repository.loadTaskDefinitions()
            val wateringTasks = tasks.filter { it.type == TaskType.WATER }

            val incompleteCount = wateringTasks.count { task ->
                val taskId = task.id ?: task.hashCode().toString()
                !completedIds.contains(taskId)
            }

            if (incompleteCount > 0) {
                Notification.showWateringReminder(
                    applicationContext,
                    "У вас $incompleteCount невыполненных задач по поливу растений"
                )
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val request = PeriodicWorkRequestBuilder<WateringCheck>(
                1, TimeUnit.HOURS   // проверка раз в час
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "watering_check",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}