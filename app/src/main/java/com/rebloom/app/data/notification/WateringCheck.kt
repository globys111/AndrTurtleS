package com.rebloom.app.data.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rebloom.app.data.local.TaskCompletionStore
import com.rebloom.app.data.repository.UserPlantRepository
import com.rebloom.app.domain.usecase.TaskScheduler
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class WateringCheck(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            TaskCompletionStore.init(applicationContext)
            val plants = UserPlantRepository(applicationContext).observePlants().first()
            val today = LocalDate.now()
            val defs = TaskScheduler.generateWateringDefinitions(plants, today)
            val occurrences = TaskScheduler.generateOccurrences(defs, today)
            val todayTasks = TaskScheduler.tasksForDate(occurrences, today)

            val completedIds = TaskCompletionStore.completedIds.value
            val incompleteCount = todayTasks.count { !completedIds.contains(it.completionKey) }

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
                1, TimeUnit.HOURS
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