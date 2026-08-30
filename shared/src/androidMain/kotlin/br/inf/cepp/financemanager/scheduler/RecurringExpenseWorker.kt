package br.inf.cepp.financemanager.scheduler

import android.content.Context
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.inf.cepp.financemanager.database.AppDatabase
import br.inf.cepp.financemanager.getDatabaseBuilder
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseStatus
import br.inf.cepp.financemanager.model.RecurrenceFrequency
import br.inf.cepp.financemanager.model.RecurrenceRule
import br.inf.cepp.financemanager.util.today
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

class RecurringExpenseWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = buildDatabase()
        return try {
            val today = today()
            val existingExpenses = database.expenseDao().getAll().first()

            val dueExpenses = existingExpenses.filter { expense ->
                expense.status == ExpenseStatus.PLANNED &&
                        expense.recurrence != null &&
                        expense.date <= today &&
                        isDueOn(expense.date, today, expense.recurrence) &&
                        !hasGeneratedExpenseForDate(existingExpenses, expense, today) &&
                        isWithinOccurrenceCount(expense.date, today, expense.recurrence)
            }

            dueExpenses.forEach { expense ->
                database.expenseDao().insert(
                    expense.copy(
                        id = 0,
                        date = today,
                        status = ExpenseStatus.CONFIRMED
                    )
                )
            }

            Result.success()
        } catch (_: Exception) {
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

    private fun hasGeneratedExpenseForDate(
        allExpenses: List<Expense>,
        baseExpense: Expense,
        targetDate: LocalDate
    ): Boolean {
        return allExpenses.any { expense ->
            expense.id != baseExpense.id &&
                    expense.description == baseExpense.description &&
                    expense.category == baseExpense.category &&
                    expense.amount == baseExpense.amount &&
                    expense.source == baseExpense.source &&
                    expense.status == ExpenseStatus.CONFIRMED &&
                    expense.recurrence == baseExpense.recurrence &&
                    expense.reminder == baseExpense.reminder &&
                    expense.date == targetDate
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
}
