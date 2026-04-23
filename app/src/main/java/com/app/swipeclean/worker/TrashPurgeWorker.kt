package com.app.swipeclean.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.app.swipeclean.data.repository.TrashRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

// @HiltWorker allows WorkManager workers to use @Inject (via Hilt)
@HiltWorker
class TrashPurgeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val trashRepo: TrashRepository
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            trashRepo.purgeExpired() // Deletes entries past their 30-day TTL
            Result.success()
        } catch (e: Exception) {
            Result.retry()  // WorkManager will reschedule automatically here
        }
    }

    companion object {
        const val WORK_NAME = "trash_purge_periodic"

        // call the below function when the homescreen first loads
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<TrashPurgeWorker>(
                repeatInterval = 1, repeatIntervalTimeUnit = TimeUnit.DAYS
            ).setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build()).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

}