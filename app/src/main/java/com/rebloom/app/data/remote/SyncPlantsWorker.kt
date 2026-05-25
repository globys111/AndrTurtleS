package com.rebloom.app.data.remote

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rebloom.app.data.repository.UserPlantRepository
import java.util.concurrent.TimeUnit

private const val TAG = "SyncWorker"

class SyncPlantsWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork: start, attempt=$runAttemptCount")
        return try {
            val allSynced = UserPlantRepository(applicationContext).syncUnsyncedWithPhotos()
            if (allSynced) {
                Log.d(TAG, "doWork: all synced")
                Result.success()
            } else {
                Log.w(TAG, "doWork: some entities failed, will retry")
                if (runAttemptCount < 3) Result.retry() else Result.failure()
            }
        } catch (e: Exception) {
            Log.e(TAG, "doWork: failed", e)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        private const val WORK_NAME = "sync_plants"

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<SyncPlantsWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)
        }
    }
}
