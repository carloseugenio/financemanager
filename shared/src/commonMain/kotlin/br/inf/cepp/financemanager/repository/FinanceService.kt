package br.inf.cepp.financemanager.repository

import br.inf.cepp.financemanager.domain.FinancialSummaryCalculator
import br.inf.cepp.financemanager.domain.CashflowSummary
import br.inf.cepp.financemanager.database.AppDatabase
import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.ui.util.lucidIconVector
import br.inf.cepp.financemanager.ui.util.toComposeColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

class FinanceService(private val database: AppDatabase) : IFinanceService {
    private val financialSummaryCalculator = FinancialSummaryCalculator()
    private val expenseDao = database.expenseDao()
    private val incomeDao = database.incomeDao()
    private val accountDao = database.accountDao()
    private val categoryDao = database.expenseCategoryDao()
    private val itemDao = database.expenseItemDao()
    private val projectDao = database.projectDao()

    override fun getMonthlyExpensesData(month: Month): Flow<List<MonthlyExpensePerCategoryViewData>> {
        return expenseDao.getAll().map { allExpenses ->
            val confirmedInMonth = allExpenses
                .filter { it.status == ExpenseStatus.CONFIRMED && it.date.month == month }

            val summary = financialSummaryCalculator.summarizeMonthlyExpenses(confirmedInMonth, "unknown")
            if (summary.total.minorUnits == 0L) return@map emptyList()

            summary.categorySummaries.map { categorySummary ->
                MonthlyExpensePerCategoryViewData(
                    name = categorySummary.category.name,
                    amount = categorySummary.total.toMajorUnits(),
                    percentage = categorySummary.percentage,
                    color = categorySummary.category.color.toComposeColor(),
                    icon = categorySummary.category.iconKey.lucidIconVector()
                )
            }
        }
    }

    override fun getTotalExpensesWithCurrencySymbol(symbol: String, month: Month): Flow<String> {
        return expenseDao.getAll().map { allExpenses ->
            val confirmedInMonth = allExpenses
                .filter { it.status == ExpenseStatus.CONFIRMED && it.date.month == month }
            val total = financialSummaryCalculator.summarizeMonthlyExpenses(confirmedInMonth, symbol).total
            "$symbol ${"%.2f".format(total.toMajorUnits())}"
        }
    }

    override fun getTotalExpenses(month: Month): Flow<Double> {
        return expenseDao.getAll().map { allExpenses ->
            val summary = financialSummaryCalculator.summarizeMonthlyExpenses(
                allExpenses.filter { it.status == ExpenseStatus.CONFIRMED && it.date.month == month },
                "unknown"
            )
            summary.total.toMajorUnits()
        }
    }

    override fun getAllIncomes(): Flow<List<Income>> = incomeDao.getAll()

    override fun getPlannedIncomes(): Flow<List<Income>> = incomeDao.getByStatus(ExpenseStatus.PLANNED)

    override fun getAllFinancialRecords(): Flow<List<FinancialRecord>> {
        return combine(expenseDao.getAll(), incomeDao.getAll()) { expenses, incomes ->
            val expenseRecords = expenses.map { OutgoingRecord(it) }
            val incomeRecords = incomes.map { it.toIncomingRecord() }
            (expenseRecords + incomeRecords).sortedWith(
                compareByDescending<FinancialRecord> { it.date }
                    .thenBy { it.type }
                    .thenBy { it.description }
            )
        }
    }

    override fun getMonthlyCashflow(month: Month): Flow<CashflowSummary> {
        return getAllFinancialRecords().map { records ->
            financialSummaryCalculator.summarizeMonthlyCashflow(records, month, "unknown")
        }
    }

    override fun getAccounts(): Flow<List<Account>> = accountDao.getAll()

    override fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAll()

    override fun getDraftExpenses(): Flow<List<Expense>> = expenseDao.getByStatus(ExpenseStatus.DRAFT)

    override fun getCategories(): Flow<List<ExpenseCategory>> = categoryDao.getAll()

    override fun getProjectPlans(): Flow<List<ProjectPlan>> = projectDao.getAllPlans()

    override fun getProjectItems(planName: String): Flow<List<ProjectItem>> = projectDao.getItemsForPlan(planName)
    
    override fun getPlannedExpenses(): Flow<List<ExpenseItem>> = itemDao.getAll()
    
    override suspend fun saveExpense(expense: Expense): Long {
        return expenseDao.insert(expense)
    }

    override suspend fun saveIncome(income: Income): Long {
        return incomeDao.insert(income)
    }

    override suspend fun saveAccount(account: Account) {
        accountDao.insert(account)
    }

    override suspend fun saveCategory(category: ExpenseCategory) {
        categoryDao.insert(category)
    }

    override suspend fun updateCategory(category: ExpenseCategory) {
        categoryDao.update(category)
    }

    override suspend fun deleteCategory(categoryName: String) {
        categoryDao.deleteByName(categoryName)
    }

    override suspend fun saveProjectPlan(plan: ProjectPlan) {
        projectDao.insertPlan(plan)
    }

    override suspend fun saveProjectItem(item: ProjectItem) {
        projectDao.insertItem(item)
    }

    override suspend fun deleteProjectItem(item: ProjectItem) {
        projectDao.deleteItem(item)
    }

    override suspend fun confirmExpense(expenseId: Long) {
        val expense = expenseDao.getById(expenseId) ?: return
        expenseDao.update(expense.copy(status = ExpenseStatus.CONFIRMED))
    }

    override suspend fun confirmAllDrafts() {
        val drafts = expenseDao.getByStatus(ExpenseStatus.DRAFT).first()
        drafts.forEach { draft ->
            expenseDao.update(draft.copy(status = ExpenseStatus.CONFIRMED))
        }
    }
}
