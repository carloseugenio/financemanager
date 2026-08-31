package br.inf.cepp.financemanager

import br.inf.cepp.financemanager.model.Account
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.model.ExpenseItem
import br.inf.cepp.financemanager.model.ExpenseSource
import br.inf.cepp.financemanager.model.ExpenseStatus
import br.inf.cepp.financemanager.model.FinanceInstitution
import br.inf.cepp.financemanager.model.FinancialRecord
import br.inf.cepp.financemanager.model.Income
import br.inf.cepp.financemanager.model.MonthlyExpensePerCategoryViewData
import br.inf.cepp.financemanager.model.ProjectItem
import br.inf.cepp.financemanager.model.ProjectPlan
import br.inf.cepp.financemanager.model.ProjectStatus
import br.inf.cepp.financemanager.repository.IFinanceService
import br.inf.cepp.financemanager.domain.CashflowSummary
import br.inf.cepp.financemanager.ui.screen.ExportViewModel
import br.inf.cepp.financemanager.ui.util.HexColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportViewModelTest {
    @Test
    fun exportMonthlyCsvDoesNotCrashOnEmptyResult() {
        val category = ExpenseCategory("Food", HexColor("#000000"), "cart")
        val service = fakeService(category)
        val viewModel = ExportViewModel(service)

        viewModel.exportMonthlyCsv("monthly", LocalDate(2026, Month.JANUARY, 1), LocalDate(2026, Month.JANUARY, 31))

        Thread.sleep(500)
        assertTrue(viewModel.lastExportStatus.value?.contains("failed", ignoreCase = true) != true)
    }

    @Test
    fun exportReportsCancellationWhenSaverReturnsBlank() {
        val category = ExpenseCategory("Food", HexColor("#000000"), "cart")
        val service = fakeService(category)
        val viewModel = ExportViewModel(service, fileSaver = { _, _, _ -> "" })

        viewModel.exportMonthlyCsv("monthly", LocalDate(2026, Month.JANUARY, 1), LocalDate(2026, Month.JANUARY, 31))

        Thread.sleep(500)
        assertEquals("Export cancelled", viewModel.lastExportStatus.value)
    }

    @Test
    fun shareFailureIsReported() {
        val category = ExpenseCategory("Food", HexColor("#000000"), "cart")
        val service = fakeService(category)
        val viewModel = ExportViewModel(service, fileSharer = { _, _ -> "error: denied" })

        viewModel.shareFilePath("/tmp/file.csv", "text/csv")

        waitForStatus(viewModel, "Share failed")
    }

    private fun fakeService(category: ExpenseCategory): IFinanceService {
        return object : IFinanceService {
            override fun getMonthlyExpensesData(month: Month): Flow<List<MonthlyExpensePerCategoryViewData>> = flowOf(emptyList())
            override fun getTotalExpensesWithCurrencySymbol(symbol: String, month: Month) = flowOf("$symbol 0.00")
            override fun getTotalExpenses(month: Month) = flowOf(0.0)
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
            override fun getAccounts(): Flow<List<Account>> = flowOf(emptyList())
            override fun getAllExpenses(): Flow<List<Expense>> = flowOf(emptyList())
            override fun getDraftExpenses(): Flow<List<Expense>> = flowOf(emptyList())
            override fun getCategories(): Flow<List<ExpenseCategory>> = flowOf(listOf(category))
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

    private fun waitForStatus(viewModel: ExportViewModel, expected: String) {
        repeat(20) {
            if (viewModel.lastExportStatus.value?.contains(expected, ignoreCase = true) == true) {
                return
            }
            Thread.sleep(100)
        }
        assertTrue(viewModel.lastExportStatus.value?.contains(expected, ignoreCase = true) == true)
    }
}
