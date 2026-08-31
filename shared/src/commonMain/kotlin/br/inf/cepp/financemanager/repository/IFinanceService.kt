package br.inf.cepp.financemanager.repository

import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.domain.CashflowSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Month
import kotlinx.datetime.LocalDate

interface IFinanceService {
    fun getMonthlyExpensesData(month: Month): Flow<List<MonthlyExpensePerCategoryViewData>>
    fun getTotalExpensesWithCurrencySymbol(symbol: String, month: Month): Flow<String>
    fun getTotalExpenses(month: Month): Flow<Double>
    fun getAllIncomes(): Flow<List<Income>> = kotlinx.coroutines.flow.flowOf(emptyList())
    fun getAllFinancialRecords(): Flow<List<FinancialRecord>> = kotlinx.coroutines.flow.flowOf(emptyList())
    fun getMonthlyCashflow(month: Month): Flow<CashflowSummary> =
        kotlinx.coroutines.flow.flowOf(
            CashflowSummary(
                incomingTotal = br.inf.cepp.financemanager.domain.Money.zero("unknown"),
                outgoingTotal = br.inf.cepp.financemanager.domain.Money.zero("unknown"),
                netTotal = br.inf.cepp.financemanager.domain.Money.zero("unknown"),
                transactionCount = 0,
                incomingCount = 0,
                outgoingCount = 0
            )
        )
    fun getAccounts(): Flow<List<Account>>
    fun getAllExpenses(): Flow<List<Expense>>
    fun getDraftExpenses(): Flow<List<Expense>>
    fun getPlannedIncomes(): Flow<List<Income>> = kotlinx.coroutines.flow.flowOf(emptyList())
    fun getCategories(): Flow<List<ExpenseCategory>>
    fun getProjectPlans(): Flow<List<ProjectPlan>>
    fun getProjectItems(planName: String): Flow<List<ProjectItem>>
    fun getPlannedExpenses(): Flow<List<ExpenseItem>>
    suspend fun saveExpense(expense: Expense): Long
    suspend fun saveIncome(income: Income): Long = 0L
    suspend fun saveAccount(account: Account)
    suspend fun saveCategory(category: ExpenseCategory)
    suspend fun updateCategory(category: ExpenseCategory)
    suspend fun deleteCategory(categoryName: String)
    suspend fun saveProjectPlan(plan: ProjectPlan)
    suspend fun saveProjectItem(item: ProjectItem)
    suspend fun deleteProjectItem(item: ProjectItem)
    suspend fun confirmExpense(expenseId: Long)
    suspend fun confirmAllDrafts()
}
