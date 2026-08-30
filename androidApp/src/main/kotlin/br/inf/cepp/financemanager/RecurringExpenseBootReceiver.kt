package br.inf.cepp.financemanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import br.inf.cepp.financemanager.scheduler.AndroidRecurringExpenseScheduler

/**
 * Boot-aware receiver for recurring expense scheduler.
 * Ensures the worker is rescheduled when the device boots or the app is installed/updated.
 * Android WorkManager normally persists scheduled work across reboots, but this provides
 * an explicit safety net for edge cases.
 */
class RecurringExpenseBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON" -> {
                Log.d(TAG, "Device boot detected; restarting recurring expense scheduler")
                AndroidRecurringExpenseScheduler(context).start()
            }
        }
    }

    companion object {
        private const val TAG = "RecurringExpenseBootReceiver"
    }
}
