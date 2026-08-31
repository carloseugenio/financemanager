package br.inf.cepp.financemanager.scheduler

import br.inf.cepp.financemanager.database.AppDatabase
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseStatus
import br.inf.cepp.financemanager.model.RecurringExpenseState
import br.inf.cepp.financemanager.util.today
import kotlinx.datetime.LocalDate

data class RecurringExpenseRunResult(
    val loadedRecurringExpenses: Int,
    val dueExpenses: Int,
    val initializedStates: Int,
    val updatedStates: Int
)

object RecurringExpenseProcessor {
    suspend fun process(
        database: AppDatabase,
        currentDate: LocalDate = today()
    ): RecurringExpenseRunResult {
        val expenseDao = database.expenseDao()
        val stateDao = database.recurringExpenseStateDao()

        val recurringExpenses = expenseDao.getByStatusWithRecurrence(ExpenseStatus.PLANNED)
        val confirmedExpenses = expenseDao.getByStatusSuspend(ExpenseStatus.CONFIRMED)
        val existingStates = stateDao.getAll().associateBy { it.sourceExpenseId }

        var initializedStates = 0
        recurringExpenses.forEach { expense ->
            val rule = expense.recurrence ?: return@forEach
            if (!existingStates.containsKey(expense.id)) {
                val nextRunDate = RecurringExpenseScheduleUtils.nextRunDateOnOrAfter(expense.date, currentDate, rule)
                    ?: return@forEach
                stateDao.insert(
                    RecurringExpenseState(
                        sourceExpenseId = expense.id,
                        nextRunDate = nextRunDate,
                        remainingCount = rule.count,
                        lastMaterializedDate = null
                    )
                )
                initializedStates++
            }
        }

        val states = stateDao.getAll().associateBy { it.sourceExpenseId }
        val dueExpenses = recurringExpenses.filter { expense ->
            val rule = expense.recurrence ?: return@filter false
            val state = states[expense.id] ?: return@filter false
            state.nextRunDate <= currentDate &&
                RecurringExpenseScheduleUtils.isDueOn(expense.date, currentDate, rule) &&
                RecurringExpenseScheduleUtils.isWithinOccurrenceCount(expense.date, currentDate, rule) &&
                !RecurringExpenseScheduleUtils.hasMaterializedExpenseForDate(confirmedExpenses, expense, currentDate)
        }

        var updatedStates = 0
        dueExpenses.forEach { baseExpense ->
            val materialized = baseExpense.copy(
                id = 0,
                date = currentDate,
                status = ExpenseStatus.CONFIRMED
            )
            expenseDao.insert(materialized)
            updateRecurringState(database, baseExpense, currentDate)
            if (materialized.reminder?.enabled == true) {
                postReminderNotification(materialized)
            }
            updatedStates++
        }

        return RecurringExpenseRunResult(
            loadedRecurringExpenses = recurringExpenses.size,
            dueExpenses = dueExpenses.size,
            initializedStates = initializedStates,
            updatedStates = updatedStates
        )
    }

    private suspend fun updateRecurringState(
        database: AppDatabase,
        sourceExpense: Expense,
        materializedDate: LocalDate
    ) {
        val rule = sourceExpense.recurrence ?: return
        val stateDao = database.recurringExpenseStateDao()
        val currentState = stateDao.getBySourceExpenseId(sourceExpense.id)

        if (currentState == null) return

        val occurrenceNumber = RecurringExpenseScheduleUtils.recurrenceOccurrenceNumber(sourceExpense.date, materializedDate, rule)
        val remainingCount = rule.count?.let { count -> count - occurrenceNumber }
        if (remainingCount != null && remainingCount <= 0) {
            stateDao.delete(currentState)
            return
        }

        val nextRunDate = RecurringExpenseScheduleUtils.computeNextRunDate(materializedDate, rule)
        if (rule.until != null && nextRunDate > rule.until) {
            stateDao.delete(currentState)
            return
        }

        stateDao.insert(
            RecurringExpenseState(
                sourceExpenseId = sourceExpense.id,
                nextRunDate = nextRunDate,
                remainingCount = remainingCount,
                lastMaterializedDate = materializedDate
            )
        )
    }
}
