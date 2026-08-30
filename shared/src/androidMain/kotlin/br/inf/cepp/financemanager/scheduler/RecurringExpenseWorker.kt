package br.inf.cepp.financemanager.scheduler

import android.content.Context
import android.util.Log
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.inf.cepp.financemanager.database.AppDatabase
import br.inf.cepp.financemanager.getDatabaseBuilder
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseStatus
import br.inf.cepp.financemanager.model.RecurrenceFrequency
import br.inf.cepp.financemanager.model.RecurrenceRule
import br.inf.cepp.financemanager.model.RecurringExpenseState
import br.inf.cepp.financemanager.util.today
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate

/**
 * Worker that materializes recurring expenses into confirmed expenses.
 * - Loads only PLANNED expenses with recurrence (efficient query)
 * - Uses timezone-safe LocalDate computation
 * - Deduplicates by (source, date) tuple (idempotent)
 * - Persists state: nextRunDate and remainingCount for reliable scheduling
 * - Keeps reminder dispatch as an extension point hook
 */
class RecurringExpenseWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = buildDatabase()
        return try {
            val today = today()
            Log.d(TAG, "RecurringExpenseWorker doWork() starting at $today")

            val recurringExpenses = database.expenseDao()
                .getByStatusWithRecurrence(ExpenseStatus.PLANNED)
            Log.d(TAG, "Loaded ${recurringExpenses.size} recurring expenses")

            val allConfirmed = database.expenseDao()
                .getByStatusSuspend(ExpenseStatus.CONFIRMED)

            val dueExpenses = recurringExpenses.filter { expense ->
                expense.recurrence != null &&
                        expense.date <= today &&
                        isDueOn(expense.date, today, expense.recurrence) &&
                        isWithinOccurrenceCount(expense.date, today, expense.recurrence) &&
                        !hasMaterializedExpenseForDate(allConfirmed, expense, today)
            }

            Log.d(TAG, "Found ${dueExpenses.size} due recurring expenses to materialize")

            dueExpenses.forEach { baseExpense ->
                val materialized = baseExpense.copy(
                    id = 0,
                    date = today,
                    status = ExpenseStatus.CONFIRMED
                )
                database.expenseDao().insert(materialized)
                Log.d(TAG, "Materialized expense: ${baseExpense.description} on $today")

                // Update state: compute next run date and remaining count
                updateRecurringState(database, baseExpense, today)

                // Extension point: dispatch reminder if configured
                onReminderDispatch(materialized)
            }

            Log.d(TAG, "RecurringExpenseWorker completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "RecurringExpenseWorker failed", e)
            Result.retry()
        } finally {
            database.close()
        }
    }

    /**
     * Updates RecurringExpenseState after materialization.
     * Computes nextRunDate and decrements remainingCount.
     */
    private suspend fun updateRecurringState(
        database: AppDatabase,
        sourceExpense: Expense,
        materializedDate: LocalDate
    ) {
        if (sourceExpense.recurrence == null || sourceExpense.id == 0L) {
            return
        }

        val nextDate = computeNextRunDate(sourceExpense.date, materializedDate, sourceExpense.recurrence)
        val remainingCount = if (sourceExpense.recurrence.count != null) {
            val occurrenceNum = recurrenceOccurrenceNumber(sourceExpense.date, materializedDate, sourceExpense.recurrence)
            sourceExpense.recurrence.count - occurrenceNum
        } else {
            null
        }

        val state = RecurringExpenseState(
            sourceExpenseId = sourceExpense.id,
            nextRunDate = nextDate,
            remainingCount = remainingCount,
            lastMaterializedDate = materializedDate
        )

        database.recurringExpenseStateDao().insert(state)
        Log.d(TAG, "Updated recurring state for expense ${sourceExpense.id}: nextRun=$nextDate, remaining=$remainingCount")
    }

    /**
     * Computes the next run date based on the recurrence rule.
     */
    private fun computeNextRunDate(
        startDate: LocalDate,
        lastDate: LocalDate,
        rule: RecurrenceRule
    ): LocalDate {
        return when (rule.frequency) {
            RecurrenceFrequency.DAILY -> {
                val epochDay = lastDate.toEpochDays() + rule.interval
                LocalDate.fromEpochDays(epochDay)
            }
            RecurrenceFrequency.WEEKLY -> {
                val epochDay = lastDate.toEpochDays() + (7L * rule.interval)
                LocalDate.fromEpochDays(epochDay)
            }
            RecurrenceFrequency.MONTHLY -> {
                addMonthsToDate(lastDate, rule.interval)
            }
            RecurrenceFrequency.YEARLY -> {
                LocalDate(
                    lastDate.year + rule.interval,
                    lastDate.monthNumber,
                    lastDate.dayOfMonth
                )
            }
        }
    }

    /**
     * Add months to a date, handling day-of-month edge cases (e.g., Jan 31 + 1 month = Feb 28).
     */
    private fun addMonthsToDate(date: LocalDate, months: Int): LocalDate {
        var newMonth = date.monthNumber + months
        var newYear = date.year

        while (newMonth > 12) {
            newMonth -= 12
            newYear++
        }

        val daysInMonth = getDaysInMonth(newMonth, newYear)
        val newDay = minOf(date.dayOfMonth, daysInMonth)

        return LocalDate(newYear, newMonth, newDay)
    }

    private fun getDaysInMonth(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 28
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    /**
     * Checks if a materialized expense already exists for the source + date tuple.
     * This provides idempotent deduplication across multiple worker runs.
     */
    private fun hasMaterializedExpenseForDate(
        confirmedExpenses: List<Expense>,
        baseExpense: Expense,
        targetDate: LocalDate
    ): Boolean {
        return confirmedExpenses.any { confirmed ->
            confirmed.source == baseExpense.source &&
                    confirmed.date == targetDate &&
                    confirmed.category == baseExpense.category
        }
    }

    private fun isWithinOccurrenceCount(
        startDate: LocalDate,
        targetDate: LocalDate,
        rule: RecurrenceRule
    ): Boolean {
        val count = rule.count ?: return true
        if (count <= 0) return false
        return recurrenceOccurrenceNumber(startDate, targetDate, rule) <= count
    }

    private fun recurrenceOccurrenceNumber(
        startDate: LocalDate,
        targetDate: LocalDate,
        rule: RecurrenceRule
    ): Int {
        if (targetDate < startDate) return 0
        return when (rule.frequency) {
            RecurrenceFrequency.DAILY -> {
                val daysBetween = targetDate.toEpochDays() - startDate.toEpochDays()
                ((daysBetween / rule.interval) + 1).toInt()
            }

            RecurrenceFrequency.WEEKLY -> {
                val daysBetween = targetDate.toEpochDays() - startDate.toEpochDays()
                ((daysBetween / (7 * rule.interval)) + 1).toInt()
            }

            RecurrenceFrequency.MONTHLY -> {
                val monthsBetween =
                    ((targetDate.year - startDate.year) * 12) +
                            (targetDate.monthNumber - startDate.monthNumber)
                ((monthsBetween / rule.interval) + 1).toInt()
            }

            RecurrenceFrequency.YEARLY -> {
                val yearsBetween = targetDate.year - startDate.year
                ((yearsBetween / rule.interval) + 1).toInt()
            }
        }
    }

    private fun isDueOn(
        startDate: LocalDate,
        targetDate: LocalDate,
        rule: RecurrenceRule
    ): Boolean {
        if (rule.interval <= 0) return false
        if (targetDate < startDate) return false
        if (rule.until != null && targetDate > rule.until) return false

        return when (rule.frequency) {
            RecurrenceFrequency.DAILY -> {
                val daysBetween = (targetDate.toEpochDays() - startDate.toEpochDays()).toLong()
                daysBetween >= 0L && daysBetween % rule.interval.toLong() == 0L
            }

            RecurrenceFrequency.WEEKLY -> {
                val daysBetween = (targetDate.toEpochDays() - startDate.toEpochDays()).toLong()
                daysBetween >= 0L &&
                        targetDate.dayOfWeek == startDate.dayOfWeek &&
                        daysBetween % (7L * rule.interval.toLong()) == 0L
            }

            RecurrenceFrequency.MONTHLY -> {
                val monthsBetween =
                    ((targetDate.year - startDate.year) * 12) +
                            (targetDate.monthNumber - startDate.monthNumber)
                monthsBetween >= 0 &&
                        monthsBetween % rule.interval == 0 &&
                        targetDate.dayOfMonth == startDate.dayOfMonth
            }

            RecurrenceFrequency.YEARLY -> {
                val yearsBetween = targetDate.year - startDate.year
                yearsBetween >= 0 &&
                        yearsBetween % rule.interval == 0 &&
                        targetDate.month == startDate.month &&
                        targetDate.dayOfMonth == startDate.dayOfMonth
            }
        }
    }

    /**
     * Extension point for reminder dispatch.
     * Currently a no-op; enable when notification infra is in place.
     */
    private suspend fun onReminderDispatch(materialized: Expense) {
        if (materialized.reminder?.enabled == true) {
            Log.d(TAG, "Reminder configured but dispatch not yet implemented: ${materialized.description}")
            // TODO: Dispatch reminder via NotificationManager or ReminderService when ready
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
