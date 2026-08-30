package br.inf.cepp.financemanager.repository

import br.inf.cepp.financemanager.database.AppDatabase
import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.ui.util.lucidIconVector
import br.inf.cepp.financemanager.ui.util.toComposeColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

class FinanceService(private val database: AppDatabase) : IFinanceService {
    private val expenseDao = database.expenseDao()
    private val accountDao = database.accountDao()
    private val categoryDao = database.expenseCategoryDao()
    private val itemDao = database.expenseItemDao()
    private val projectDao = database.projectDao()

    override fun getMonthlyExpensesData(month: Month): Flow<List<MonthlyExpensePerCategoryViewData>> {
        return expenseDao.getAll().map { allExpenses ->
            val confirmedInMonth = allExpenses
                .filter { it.status == ExpenseStatus.CONFIRMED && it.date.month == month }
            
            val totalAmount = confirmedInMonth.sumOf { it.amount }
            if (totalAmount == 0.0) return@map emptyList()

            val totalByCategory = confirmedInMonth.groupingBy { it.category }
                .fold(0.0) { acc, element -> acc + element.amount }

            totalByCategory.map { (category, sum) ->
                MonthlyExpensePerCategoryViewData(
                    name = category.name,
                    amount = sum,
                    percentage = (sum / totalAmount) * 100.0,
                    color = category.color.toComposeColor(),
                    icon = category.iconKey.lucidIconVector()
                )
            }
        }
    }

    override fun getTotalExpensesWithCurrencySymbol(symbol: String, month: Month): Flow<String> {
        return expenseDao.getAll().map { allExpenses ->
            val total = allExpenses
                .filter { it.status == ExpenseStatus.CONFIRMED && it.date.month == month }
                .sumOf { it.amount }
            "$symbol $total"
        }
    }

    override fun getTotalExpenses(month: Month): Flow<Double> {
        return expenseDao.getAll().map { allExpenses ->
            allExpenses
                .filter { it.status == ExpenseStatus.CONFIRMED && it.date.month == month }
                .sumOf { it.amount }
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
