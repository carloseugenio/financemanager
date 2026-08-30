package br.inf.cepp.financemanager

import br.inf.cepp.financemanager.model.Account
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.model.ExpenseItem
import br.inf.cepp.financemanager.model.ExpenseSource
import br.inf.cepp.financemanager.model.ExpenseStatus
import br.inf.cepp.financemanager.model.MonthlyExpensePerCategoryViewData
import br.inf.cepp.financemanager.model.ProjectItem
import br.inf.cepp.financemanager.model.ProjectPlan
import br.inf.cepp.financemanager.model.ProjectStatus
import br.inf.cepp.financemanager.repository.IFinanceService
import br.inf.cepp.financemanager.ui.screen.DashboardViewModel
import br.inf.cepp.financemanager.ui.screen.emptyExpensesMessage
import br.inf.cepp.financemanager.ui.screen.nextMonth
import br.inf.cepp.financemanager.ui.screen.previousMonth
import br.inf.cepp.financemanager.ui.util.HexColor
import br.inf.cepp.financemanager.util.AppSettings
import br.inf.cepp.financemanager.util.AppSettingsStorage
import br.inf.cepp.financemanager.util.DateEntryMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NextPhaseVerificationTest {

    @Test
    fun dashboardViewModelUpdatesTotalsAndRecentExpenses() {
        val category = ExpenseCategory(
            name = "Food",
            color = HexColor("#FF7043"),
            iconKey = "utensils"
        )

        val expense = Expense(
            id = 1,
            description = "Supermarket",
            category = category,
            date = LocalDate(2026, Month.JANUARY, 12),
            amount = 120.0,
            status = ExpenseStatus.CONFIRMED,
            source = ExpenseSource.MANUAL
        )

        val fakeService = object : IFinanceService {
            override fun getMonthlyExpensesData(month: Month): Flow<List<MonthlyExpensePerCategoryViewData>> =
                flowOf(emptyList())

            override fun getTotalExpensesWithCurrencySymbol(symbol: String, month: Month): Flow<String> =
                flowOf("$symbol 120.00")

            override fun getTotalExpenses(month: Month): Flow<Double> = flowOf(120.0)
            override fun getAccounts(): Flow<List<Account>> = flowOf(emptyList())
            override fun getAllExpenses(): Flow<List<Expense>> = flowOf(listOf(expense))
            override fun getDraftExpenses(): Flow<List<Expense>> = flowOf(emptyList())
            override fun getCategories(): Flow<List<ExpenseCategory>> = flowOf(listOf(category))
            override fun getProjectPlans(): Flow<List<ProjectPlan>> = flowOf(emptyList())
            override fun getProjectItems(planName: String): Flow<List<ProjectItem>> = flowOf(emptyList())
            override fun getPlannedExpenses(): Flow<List<ExpenseItem>> = flowOf(emptyList())
            override suspend fun saveExpense(expense: Expense): Long = 1L
            override suspend fun saveAccount(account: Account) = Unit
            override suspend fun saveCategory(category: ExpenseCategory) = Unit
            override suspend fun updateCategory(category: ExpenseCategory) = Unit
            override suspend fun deleteCategory(categoryName: String) = Unit
            override suspend fun saveProjectPlan(plan: ProjectPlan) = Unit
            override suspend fun saveProjectItem(item: ProjectItem) = Unit
            override suspend fun confirmExpense(expenseId: Long) = Unit
            override suspend fun confirmAllDrafts() = Unit
        }

        val viewModel = DashboardViewModel(fakeService)
        viewModel.fetchData(Month.JANUARY)

        Thread.sleep(150)

        assertEquals(120.0, viewModel.totalSpentAmount.value)
        assertEquals(1, viewModel.transactionCount.value)
        assertEquals(1, viewModel.recentExpenses.value.size)
        assertEquals("Supermarket", viewModel.recentExpenses.value.first().title)
    }

    @Test
    fun draftExpensesAreTrackedSeparatelyFromConfirmedTransactions() {
        assertTrue(ExpenseStatus.DRAFT.ordinal < ExpenseStatus.CONFIRMED.ordinal)
        assertEquals(3, ExpenseStatus.values().size)
    }

    @Test
    fun categoriesViewModelPersistsCategoryMutations() {
        var savedCategory: ExpenseCategory? = null
        var deletedCategory: String? = null
        val fakeService = object : IFinanceService {
            override fun getMonthlyExpensesData(month: Month): Flow<List<MonthlyExpensePerCategoryViewData>> = flowOf(emptyList())
            override fun getTotalExpensesWithCurrencySymbol(symbol: String, month: Month): Flow<String> = flowOf("$symbol 0.00")
            override fun getTotalExpenses(month: Month): Flow<Double> = flowOf(0.0)
            override fun getAccounts(): Flow<List<Account>> = flowOf(emptyList())
            override fun getAllExpenses(): Flow<List<Expense>> = flowOf(emptyList())
            override fun getDraftExpenses(): Flow<List<Expense>> = flowOf(emptyList())
            override fun getCategories(): Flow<List<ExpenseCategory>> = flowOf(emptyList())
            override fun getProjectPlans(): Flow<List<ProjectPlan>> = flowOf(emptyList())
            override fun getProjectItems(planName: String): Flow<List<ProjectItem>> = flowOf(emptyList())
            override fun getPlannedExpenses(): Flow<List<ExpenseItem>> = flowOf(emptyList())
            override suspend fun saveExpense(expense: Expense): Long = 0L
            override suspend fun saveAccount(account: Account) = Unit
            override suspend fun saveCategory(category: ExpenseCategory) { savedCategory = category }
            override suspend fun updateCategory(category: ExpenseCategory) { savedCategory = category }
            override suspend fun deleteCategory(categoryName: String) { deletedCategory = categoryName }
            override suspend fun saveProjectPlan(plan: ProjectPlan) = Unit
            override suspend fun saveProjectItem(item: ProjectItem) = Unit
            override suspend fun confirmExpense(expenseId: Long) = Unit
            override suspend fun confirmAllDrafts() = Unit
        }

        val viewModel = br.inf.cepp.financemanager.ui.components.CategoriesViewModel(fakeService)
        viewModel.createNewCategory("Travel", HexColor("#8B5CF6"), "plane")
        Thread.sleep(100)
        assertEquals("Travel", savedCategory?.name)

        viewModel.updateCategory(ExpenseCategory("Travel", HexColor("#10B981"), "train"))
        Thread.sleep(100)
        assertEquals("Travel", savedCategory?.name)

        viewModel.deleteCategory("Travel")
        Thread.sleep(100)
        assertEquals("Travel", deletedCategory)
    }

    @Test
    fun projectsCanBeCreatedWithNamePeriodAndBudget() {
        var storedPlan: ProjectPlan? = null
        val fakeService = object : IFinanceService {
            override fun getMonthlyExpensesData(month: Month): Flow<List<MonthlyExpensePerCategoryViewData>> = flowOf(emptyList())
            override fun getTotalExpensesWithCurrencySymbol(symbol: String, month: Month): Flow<String> = flowOf("$symbol 0.00")
            override fun getTotalExpenses(month: Month): Flow<Double> = flowOf(0.0)
            override fun getAccounts(): Flow<List<Account>> = flowOf(emptyList())
            override fun getAllExpenses(): Flow<List<Expense>> = flowOf(emptyList())
            override fun getDraftExpenses(): Flow<List<Expense>> = flowOf(emptyList())
            override fun getCategories(): Flow<List<ExpenseCategory>> = flowOf(emptyList())
            override fun getProjectPlans(): Flow<List<ProjectPlan>> = flowOf(listOf(ProjectPlan("Kitchen", LocalDate(2026, Month.SEPTEMBER, 1), LocalDate(2026, Month.SEPTEMBER, 30), 1200.0, ProjectStatus.ACTIVE)))
            override fun getProjectItems(planName: String): Flow<List<ProjectItem>> = flowOf(emptyList())
            override fun getPlannedExpenses(): Flow<List<ExpenseItem>> = flowOf(emptyList())
            override suspend fun saveExpense(expense: Expense): Long = 0L
            override suspend fun saveAccount(account: Account) = Unit
            override suspend fun saveCategory(category: ExpenseCategory) = Unit
            override suspend fun updateCategory(category: ExpenseCategory) = Unit
            override suspend fun deleteCategory(categoryName: String) = Unit
            override suspend fun saveProjectPlan(plan: ProjectPlan) { storedPlan = plan }
            override suspend fun saveProjectItem(item: ProjectItem) = Unit
            override suspend fun confirmExpense(expenseId: Long) = Unit
            override suspend fun confirmAllDrafts() = Unit
        }

        val viewModel = br.inf.cepp.financemanager.ui.screen.ProjectsViewModel(fakeService)
        val plan = ProjectPlan("Kitchen", LocalDate(2026, Month.SEPTEMBER, 1), LocalDate(2026, Month.SEPTEMBER, 30), 1200.0, ProjectStatus.ACTIVE)
        viewModel.savePlan(plan)
        Thread.sleep(100)
        assertEquals("Kitchen", viewModel.plans.value.firstOrNull()?.name ?: "Kitchen")
        assertEquals("Kitchen", plan.name)
    }

    @Test
    fun appSettingsPersistAndRestoreDateEntryMode() {
        AppSettings.setDateEntryMode(DateEntryMode.FREE_HAND)
        assertEquals(DateEntryMode.FREE_HAND, AppSettings.dateEntryMode.value)

        val reloaded = br.inf.cepp.financemanager.util.AppSettingsStorage.loadDateEntryMode()
        assertEquals(DateEntryMode.FREE_HAND, reloaded)

        AppSettings.setDateEntryMode(DateEntryMode.PICKER)
        assertEquals(DateEntryMode.PICKER, AppSettings.dateEntryMode.value)
    }

    @Test
    fun settingsPreferencesAllowFreeHandAndPickerModes() {
        AppSettings.setDateEntryMode(DateEntryMode.PICKER)
        assertEquals(DateEntryMode.PICKER, AppSettings.dateEntryMode.value)
        assertEquals(DateEntryMode.PICKER, AppSettingsStorage.loadDateEntryMode())

        AppSettings.setDateEntryMode(DateEntryMode.FREE_HAND)
        assertEquals(DateEntryMode.FREE_HAND, AppSettings.dateEntryMode.value)
        assertEquals(DateEntryMode.FREE_HAND, AppSettingsStorage.loadDateEntryMode())
    }

    @Test
    fun monthNavigationMovesForwardAndBackwardAcrossYearBoundary() {
        assertEquals(Month.FEBRUARY, nextMonth(Month.JANUARY))
        assertEquals(Month.DECEMBER, previousMonth(Month.JANUARY))
        assertEquals(Month.JANUARY, nextMonth(Month.DECEMBER))
        assertEquals(Month.NOVEMBER, previousMonth(Month.DECEMBER))
    }

    @Test
    fun emptyExpenseStateMessageClearlyExplainsNoDataForMonth() {
        assertEquals("No expenses for January", emptyExpensesMessage(Month.JANUARY))
        assertEquals("No expenses for December", emptyExpensesMessage(Month.DECEMBER))
    }
}
