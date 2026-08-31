package br.inf.cepp.financemanager

import br.inf.cepp.financemanager.model.Account
import br.inf.cepp.financemanager.model.AccountType
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.model.ExpenseItem
import br.inf.cepp.financemanager.model.ExpenseStatus
import br.inf.cepp.financemanager.model.FinanceInstitution
import br.inf.cepp.financemanager.model.FinancialRecord
import br.inf.cepp.financemanager.model.Income
import br.inf.cepp.financemanager.model.MonthlyExpensePerCategoryViewData
import br.inf.cepp.financemanager.model.ProjectItem
import br.inf.cepp.financemanager.model.ProjectPlan
import br.inf.cepp.financemanager.repository.IFinanceService
import br.inf.cepp.financemanager.domain.CashflowSummary
import br.inf.cepp.financemanager.ui.screen.AccountsViewModel
import br.inf.cepp.financemanager.ui.util.HexColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccountsViewModelTest {
    @Test
    fun accountsViewModelBuildsSummaryAndCreditUtilization() {
        val institution = FinanceInstitution("North Bank", "Bank")
        val accounts = listOf(
            Account("Checking", AccountType.CHECKING, institution, "0001", "12345-6", "PIX", 2500.0, 5, 1450.0, HexColor("#22C55E"), "dollar-sign"),
            Account("Credit Card", AccountType.CREDIT, institution, "0002", "98765-4", "", 6000.0, 12, -320.5, HexColor("#EF4444"), "credit-card")
        )

        val viewModel = AccountsViewModel(fakeService(accounts))
        Thread.sleep(250)

        assertEquals(2, viewModel.accounts.value.size)
        assertTrue(viewModel.accounts.value.first().account.type == AccountType.CREDIT)
        assertEquals(2, viewModel.accountSummary.value?.accountCount)
        assertEquals(1, viewModel.creditSummary.value?.cardCount)
        assertTrue(viewModel.creditSummary.value?.utilizationPercent ?: 0.0 > 0.0)
    }

    private fun fakeService(accounts: List<Account>): IFinanceService {
        return object : IFinanceService {
            override fun getMonthlyExpensesData(month: Month): Flow<List<MonthlyExpensePerCategoryViewData>> = flowOf(emptyList())
            override fun getTotalExpensesWithCurrencySymbol(symbol: String, month: Month): Flow<String> = flowOf("$symbol 0.00")
            override fun getTotalExpenses(month: Month): Flow<Double> = flowOf(0.0)
            override fun getAllIncomes(): Flow<List<Income>> = flowOf(emptyList())
            override fun getAllFinancialRecords(): Flow<List<FinancialRecord>> = flowOf(emptyList())
            override fun getMonthlyCashflow(month: Month): Flow<CashflowSummary> = flowOf(CashflowSummary(
                incomingTotal = br.inf.cepp.financemanager.domain.Money.zero("local"),
                outgoingTotal = br.inf.cepp.financemanager.domain.Money.zero("local"),
                netTotal = br.inf.cepp.financemanager.domain.Money.zero("local"),
                transactionCount = 0,
                incomingCount = 0,
                outgoingCount = 0
            ))
            override fun getAccounts(): Flow<List<Account>> = flowOf(accounts)
            override fun getAllExpenses(): Flow<List<Expense>> = flowOf(emptyList())
            override fun getDraftExpenses(): Flow<List<Expense>> = flowOf(emptyList())
            override fun getCategories(): Flow<List<ExpenseCategory>> = flowOf(emptyList())
            override fun getProjectPlans(): Flow<List<ProjectPlan>> = flowOf(emptyList())
            override fun getProjectItems(planName: String): Flow<List<ProjectItem>> = flowOf(emptyList())
            override fun getPlannedExpenses(): Flow<List<ExpenseItem>> = flowOf(emptyList())
            override suspend fun saveExpense(expense: Expense): Long = 0L
            override suspend fun saveIncome(income: Income): Long = 0L
            override suspend fun saveAccount(account: Account) = Unit
            override suspend fun saveCategory(category: ExpenseCategory) = Unit
            override suspend fun updateCategory(category: ExpenseCategory) = Unit
            override suspend fun deleteCategory(categoryName: String) = Unit
            override suspend fun saveProjectPlan(plan: ProjectPlan) = Unit
            override suspend fun saveProjectItem(item: ProjectItem) = Unit
            override suspend fun deleteProjectItem(item: ProjectItem) = Unit
            override suspend fun confirmExpense(expenseId: Long) = Unit
            override suspend fun confirmAllDrafts() = Unit
        }
    }
}
