package br.inf.cepp.financemanager.scheduler

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Android implementation of recurring expense scheduler using WorkManager.
 * - Schedules unique periodic work (24-hour interval, replaceable on update)
 * - Adds resilience: backoff policy for failures, battery-safe constraints
 * - Boot-aware: work persists across app restarts
 * - Provides explicit start/stop/cancel control with logging
 */
class AndroidRecurringExpenseScheduler(private val context: Context) : RecurringExpenseScheduler {
    
    override fun start() {
        Log.d(TAG, "Starting recurring expense scheduler")
        
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiresStorageNotLow(true)
            .build()
        
        val request = PeriodicWorkRequestBuilder<RecurringExpenseWorker>(
            24, TimeUnit.HOURS
        ).apply {
            setConstraints(constraints)
            addTag(WORK_TAG)
        }.build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        
        Log.d(TAG, "Recurring expense work scheduled with tag=$WORK_TAG")
    }

    override fun stop() {
        Log.d(TAG, "Stopping recurring expense scheduler")
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
    
    /**
     * Explicitly cancel all recurring expense work by tag.
     * More aggressive than stop() — cancels all work tagged with WORK_TAG,
     * useful for app cleanup or complete scheduler reset.
     */
    override fun cancel() {
        Log.d(TAG, "Cancelling all recurring expense work (tag=$WORK_TAG)")
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
    }

    companion object {
        private const val TAG = "AndroidRecurringExpenseScheduler"
        private const val WORK_NAME = "recurring-expense-worker"
        private const val WORK_TAG = "recurring-expense"
    }
}

