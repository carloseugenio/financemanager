package br.inf.cepp.financemanager.scheduler

import android.content.Context
import android.util.Log
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.inf.cepp.financemanager.database.AppDatabase
import br.inf.cepp.financemanager.getDatabaseBuilder
import br.inf.cepp.financemanager.util.today
import kotlinx.coroutines.Dispatchers

/**
 * Worker that materializes recurring expenses into confirmed expenses.
 */
class RecurringExpenseWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = buildDatabase()
        return try {
            val result = RecurringExpenseProcessor.process(database, today())
            Log.d(
                TAG,
                "RecurringExpenseWorker completed: loaded=${result.loadedRecurringExpenses}, due=${result.dueExpenses}, " +
                    "initialized=${result.initializedStates}, updated=${result.updatedStates}"
            )
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "RecurringExpenseWorker failed", e)
            Result.retry()
        } finally {
            database.close()
        }
    }

    private fun buildDatabase(): AppDatabase {
        return getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    companion object {
        private const val TAG = "RecurringExpenseWorker"
    }
}
