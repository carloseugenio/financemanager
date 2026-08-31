package br.inf.cepp.financemanager

import br.inf.cepp.financemanager.domain.FinancialSummaryCalculator
import br.inf.cepp.financemanager.domain.Money
import br.inf.cepp.financemanager.model.Account
import br.inf.cepp.financemanager.model.AccountType
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.model.ExpenseSource
import br.inf.cepp.financemanager.model.ExpenseStatus
import br.inf.cepp.financemanager.model.FinancialRecord
import br.inf.cepp.financemanager.model.OutgoingRecord
import br.inf.cepp.financemanager.model.IncomingRecord
import br.inf.cepp.financemanager.model.IncomeCategory
import br.inf.cepp.financemanager.model.FinanceInstitution
import br.inf.cepp.financemanager.model.ProjectItem
import br.inf.cepp.financemanager.model.ProjectPlan
import br.inf.cepp.financemanager.model.ProjectStatus
import br.inf.cepp.financemanager.ui.util.HexColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FinancialCalculationsTest {
    private val calculator = FinancialSummaryCalculator()

    @Test
    fun moneyRoundsToMinorUnits() {
        val money = Money.fromMajorUnits(10.235, "USD")

        assertEquals(1024L, money.minorUnits)
        assertTrue(kotlin.math.abs(money.toMajorUnits() - 10.24) < 0.0001)
    }

    @Test
    fun monthlySummaryAggregatesTotalsAndCategories() {
        val groceries = ExpenseCategory("Groceries", HexColor("#00AA00"), "cart")
        val transport = ExpenseCategory("Transport", HexColor("#0000AA"), "car")
        val expenses = listOf(
            Expense(id = 1, description = "Market", category = groceries, date = LocalDate(2026, Month.JANUARY, 1), amount = 10.239, status = ExpenseStatus.CONFIRMED, source = ExpenseSource.MANUAL),
            Expense(id = 2, description = "Fuel", category = transport, date = LocalDate(2026, Month.JANUARY, 2), amount = 4.101, status = ExpenseStatus.CONFIRMED, source = ExpenseSource.MANUAL),
            Expense(id = 3, description = "Bread", category = groceries, date = LocalDate(2026, Month.JANUARY, 3), amount = 3.0, status = ExpenseStatus.CONFIRMED, source = ExpenseSource.MANUAL),
        )

        val summary = calculator.summarizeMonthlyExpenses(expenses, "USD")

        assertTrue(kotlin.math.abs(summary.total.toMajorUnits() - 17.34) < 0.0001)
        assertEquals(3, summary.transactionCount)
        assertEquals(2, summary.categorySummaries.size)
        assertEquals("Groceries", summary.categorySummaries.first().category.name)
        assertTrue(kotlin.math.abs(summary.categorySummaries.first().total.toMajorUnits() - 13.24) < 0.0001)
        assertTrue(summary.categorySummaries.first().percentage > summary.categorySummaries.last().percentage)
    }

    @Test
    fun accountSummaryAddsBalancesAndLimits() {
        val bank = FinanceInstitution("North Bank", "Bank")
        val accounts = listOf(
            Account("Checking", AccountType.CHECKING, bank, "0001", "123", "", 2000.0, 1, 1000.125, HexColor("#00FF00"), "wallet"),
            Account("Card", AccountType.CREDIT, bank, "0002", "456", "", 5000.0, 10, -25.335, HexColor("#FF0000"), "credit-card"),
        )

        val summary = calculator.summarizeAccounts(accounts, "USD")

        assertEquals(2, summary.accountCount)
        assertTrue(kotlin.math.abs(summary.totalBalance.toMajorUnits() - 974.79) < 0.0001)
        assertTrue(kotlin.math.abs(summary.totalLimit.toMajorUnits() - 7000.0) < 0.0001)
        assertTrue(summary.utilizationPercent > 0.0)
    }

    @Test
    fun projectBudgetVsActualUsesRoundedItemTotals() {
        val category = ExpenseCategory("Travel", HexColor("#111111"), "plane")
        val project = ProjectPlan(
            name = "Trip",
            startDate = LocalDate(2026, Month.SEPTEMBER, 1),
            endDate = LocalDate(2026, Month.SEPTEMBER, 30),
            budget = 100.0,
            status = ProjectStatus.ACTIVE
        )
        val placeholder = br.inf.cepp.financemanager.model.ExpenseItem(
            id = 0L,
            title = "",
            date = LocalDate(2026, Month.SEPTEMBER, 1),
            amount = 0.0,
            iconKey = "plane",
            iconColor = HexColor("#111111")
        )
        val items = listOf(
            ProjectItem(1, project.name, category, "Flight", 25.125, 25.125, LocalDate(2026, Month.SEPTEMBER, 5), placeholder),
            ProjectItem(2, project.name, category, "Hotel", 30.125, 30.125, LocalDate(2026, Month.SEPTEMBER, 6), placeholder),
        )

        val summary = calculator.summarizeProjectBudgetVsActual(project, items, "USD")

        assertTrue(kotlin.math.abs(summary.budget.toMajorUnits() - 100.0) < 0.0001)
        assertTrue(kotlin.math.abs(summary.actual.toMajorUnits() - 55.26) < 0.0001)
        assertTrue(kotlin.math.abs(summary.variance.toMajorUnits() - (-44.74)) < 0.0001)
        assertTrue(summary.budgetUtilizationPercent > 50.0)
    }

    @Test
    fun budgetVsActualSummaryUsesSharedMoneyRules() {
        val planned = Money.fromMajorUnits(100.125, "USD")
        val actual = Money.fromMajorUnits(82.335, "USD")

        val summary = calculator.summarizeBudgetVsActual(planned, actual)

        assertTrue(kotlin.math.abs(summary.planned.toMajorUnits() - 100.13) < 0.0001)
        assertTrue(kotlin.math.abs(summary.actual.toMajorUnits() - 82.34) < 0.0001)
        assertTrue(kotlin.math.abs(summary.variance.toMajorUnits() - (-17.79)) < 0.0001)
        assertTrue(summary.utilizationPercent > 80.0)
    }

    @Test
    fun cashflowSummarySeparatesIncomingAndOutgoingTransactions() {
        val outgoing = OutgoingRecord(
            Expense(
                id = 1,
                description = "Rent",
                category = ExpenseCategory("Housing", HexColor("#123456"), "home"),
                date = LocalDate(2026, Month.AUGUST, 3),
                amount = 800.225,
                status = ExpenseStatus.CONFIRMED,
                source = ExpenseSource.MANUAL
            )
        )

        val records: List<FinancialRecord> = listOf(
            IncomingRecord("Salary", IncomeCategory.SALARY, LocalDate(2026, Month.AUGUST, 1), 2500.125),
            IncomingRecord("Dividend", IncomeCategory.DIVIDEND, LocalDate(2026, Month.AUGUST, 2), 50.335),
            outgoing
        )

        val summary = calculator.summarizeCashflow(records, "USD")

        assertTrue(kotlin.math.abs(summary.incomingTotal.toMajorUnits() - 2550.47) < 0.0001)
        assertTrue(kotlin.math.abs(summary.outgoingTotal.toMajorUnits() - 800.23) < 0.0001)
        assertTrue(kotlin.math.abs(summary.netTotal.toMajorUnits() - 1750.24) < 0.0001)
        assertEquals(3, summary.transactionCount)
        assertEquals(2, summary.incomingCount)
        assertEquals(1, summary.outgoingCount)
    }

    @Test
    fun incomeCategorySummaryGroupsIncomingTransactions() {
        val incomes = listOf(
            IncomingRecord("Salary", IncomeCategory.SALARY, LocalDate(2026, Month.AUGUST, 1), 2500.0),
            IncomingRecord("Bonus", IncomeCategory.SALARY, LocalDate(2026, Month.AUGUST, 15), 125.125),
            IncomingRecord("Dividend", IncomeCategory.DIVIDEND, LocalDate(2026, Month.AUGUST, 20), 50.0),
        )

        val summaries = calculator.summarizeIncomeCategories(incomes, "USD")

        assertEquals(2, summaries.size)
        assertEquals(IncomeCategory.SALARY, summaries.first().category)
        assertTrue(kotlin.math.abs(summaries.first().total.toMajorUnits() - 2625.13) < 0.0001)
        assertTrue(summaries.first().percentage > summaries.last().percentage)
    }
}
