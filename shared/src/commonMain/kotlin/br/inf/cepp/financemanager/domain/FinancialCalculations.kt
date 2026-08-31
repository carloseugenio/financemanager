package br.inf.cepp.financemanager.domain

import br.inf.cepp.financemanager.model.Account
import br.inf.cepp.financemanager.model.AccountType
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.model.ExpenseItem
import br.inf.cepp.financemanager.model.FinancialRecord
import br.inf.cepp.financemanager.model.IncomingRecord
import br.inf.cepp.financemanager.model.IncomeCategory
import br.inf.cepp.financemanager.model.RecordType
import br.inf.cepp.financemanager.model.ProjectItem
import br.inf.cepp.financemanager.model.ProjectPlan
import kotlin.math.ceil
import kotlin.math.floor

private const val CENTS_PER_UNIT = 100.0

data class Money(
    val minorUnits: Long,
    val currencyLabel: String,
) {
    companion object {
        fun fromMajorUnits(amount: Double, currencyLabel: String): Money {
            return Money(roundToMinorUnits(amount), currencyLabel)
        }

        fun zero(currencyLabel: String): Money {
            return Money(0L, currencyLabel)
        }
    }

    fun toMajorUnits(): Double {
        return minorUnits.toDouble() / CENTS_PER_UNIT
    }

    operator fun plus(other: Money): Money {
        require(currencyLabel == other.currencyLabel) {
            "Cannot add money with different currency labels."
        }
        return Money(minorUnits + other.minorUnits, currencyLabel)
    }

    operator fun minus(other: Money): Money {
        require(currencyLabel == other.currencyLabel) {
            "Cannot subtract money with different currency labels."
        }
        return Money(minorUnits - other.minorUnits, currencyLabel)
    }
}

data class CategoryExpenseSummary(
    val category: ExpenseCategory,
    val total: Money,
    val percentage: Double,
)

data class MonthlyExpenseSummary(
    val total: Money,
    val categorySummaries: List<CategoryExpenseSummary>,
    val transactionCount: Int,
)

data class IncomeCategorySummary(
    val category: IncomeCategory,
    val total: Money,
    val percentage: Double,
)

data class CashflowSummary(
    val incomingTotal: Money,
    val outgoingTotal: Money,
    val netTotal: Money,
    val transactionCount: Int,
    val incomingCount: Int,
    val outgoingCount: Int,
)

data class AccountSummary(
    val totalBalance: Money,
    val totalLimit: Money,
    val accountCount: Int,
    val utilizationPercent: Double,
)

data class AccountPositionSummary(
    val account: Account,
    val balance: Money,
    val limit: Money,
    val availableAmount: Money,
    val utilizationPercent: Double,
)

data class CreditCardSummary(
    val cardCount: Int,
    val utilizedAmount: Money,
    val totalLimit: Money,
    val utilizationPercent: Double,
)

data class ProjectBudgetSummary(
    val project: ProjectPlan,
    val budget: Money,
    val actual: Money,
    val variance: Money,
    val budgetUtilizationPercent: Double,
)

data class BudgetVsActualSummary(
    val planned: Money,
    val actual: Money,
    val variance: Money,
    val utilizationPercent: Double,
)

class FinancialSummaryCalculator {
    fun summarizeMonthlyExpenses(expenses: List<Expense>, currencyLabel: String): MonthlyExpenseSummary {
        val total = sumMoney(expenses.map { it.amount }, currencyLabel)
        if (total.minorUnits == 0L) {
            return MonthlyExpenseSummary(
                total = total,
                categorySummaries = emptyList(),
                transactionCount = expenses.size,
            )
        }

        val grouped = expenses.groupBy { it.category.name }
            .map { (_, categoryExpenses) ->
                val category = categoryExpenses.first().category
                val categoryTotal = sumMoney(categoryExpenses.map { it.amount }, currencyLabel)
                CategoryExpenseSummary(
                    category = category,
                    total = categoryTotal,
                    percentage = (categoryTotal.toMajorUnits() / total.toMajorUnits()) * 100.0,
                )
            }
            .sortedWith(
                compareByDescending<CategoryExpenseSummary> { it.total.minorUnits }
                    .thenBy { it.category.name }
            )

        return MonthlyExpenseSummary(
            total = total,
            categorySummaries = grouped,
            transactionCount = expenses.size,
        )
    }

    fun summarizeIncomeCategories(incomes: List<IncomingRecord>, currencyLabel: String): List<IncomeCategorySummary> {
        val total = sumMoney(incomes.map { it.amount }, currencyLabel)
        if (total.minorUnits == 0L) {
            return emptyList()
        }

        return incomes.groupBy { it.category }
            .map { (_, categoryIncomes) ->
                val category = categoryIncomes.first().category
                val categoryTotal = sumMoney(categoryIncomes.map { it.amount }, currencyLabel)
                IncomeCategorySummary(
                    category = category,
                    total = categoryTotal,
                    percentage = (categoryTotal.toMajorUnits() / total.toMajorUnits()) * 100.0,
                )
            }
            .sortedWith(
                compareByDescending<IncomeCategorySummary> { it.total.minorUnits }
                    .thenBy { it.category.name }
            )
    }

    fun summarizeCashflow(records: List<FinancialRecord>, currencyLabel: String): CashflowSummary {
        val incoming = records.filter { it.type == RecordType.INCOME }
        val outgoing = records.filter { it.type == RecordType.EXPENSE }
        val incomingTotal = sumMoney(incoming.map { it.amount }, currencyLabel)
        val outgoingTotal = sumMoney(outgoing.map { it.amount }, currencyLabel)

        return CashflowSummary(
            incomingTotal = incomingTotal,
            outgoingTotal = outgoingTotal,
            netTotal = incomingTotal - outgoingTotal,
            transactionCount = records.size,
            incomingCount = incoming.size,
            outgoingCount = outgoing.size,
        )
    }

    fun summarizeMonthlyCashflow(records: List<FinancialRecord>, month: kotlinx.datetime.Month, currencyLabel: String): CashflowSummary {
        return summarizeCashflow(records.filter { it.date.month == month }, currencyLabel)
    }

    fun summarizeAccounts(accounts: List<Account>, currencyLabel: String): AccountSummary {
        val totalBalance = sumMoney(accounts.map { it.balance }, currencyLabel)
        val totalLimit = sumMoney(accounts.map { it.limit }, currencyLabel)
        val utilizationPercent = if (totalLimit.minorUnits == 0L) 0.0 else {
            (totalBalance.toMajorUnits() / totalLimit.toMajorUnits()) * 100.0
        }

        return AccountSummary(
            totalBalance = totalBalance,
            totalLimit = totalLimit,
            accountCount = accounts.size,
            utilizationPercent = utilizationPercent,
        )
    }

    fun summarizeAccount(account: Account, currencyLabel: String): AccountPositionSummary {
        val balance = Money.fromMajorUnits(account.balance, currencyLabel)
        val limit = Money.fromMajorUnits(account.limit, currencyLabel)
        val usedAmount = when (account.type) {
            AccountType.CREDIT -> Money.fromMajorUnits(kotlin.math.abs(account.balance), currencyLabel)
            else -> balance
        }
        val available = when (account.type) {
            AccountType.CREDIT -> limit - usedAmount
            else -> balance
        }
        val utilizationPercent = if (limit.minorUnits == 0L) 0.0 else {
            (usedAmount.toMajorUnits() / limit.toMajorUnits()) * 100.0
        }

        return AccountPositionSummary(
            account = account,
            balance = balance,
            limit = limit,
            availableAmount = available,
            utilizationPercent = utilizationPercent,
        )
    }

    fun summarizeCreditCards(accounts: List<Account>, currencyLabel: String): CreditCardSummary {
        val creditAccounts = accounts.filter { it.type == AccountType.CREDIT }
        val utilized = sumMoney(creditAccounts.map { kotlin.math.abs(it.balance) }, currencyLabel)
        val limits = sumMoney(creditAccounts.map { it.limit }, currencyLabel)
        val utilizationPercent = if (limits.minorUnits == 0L) 0.0 else {
            (utilized.toMajorUnits() / limits.toMajorUnits()) * 100.0
        }

        return CreditCardSummary(
            cardCount = creditAccounts.size,
            utilizedAmount = utilized,
            totalLimit = limits,
            utilizationPercent = utilizationPercent,
        )
    }

    fun summarizePlannedExpenses(plannedExpenses: List<ExpenseItem>, currencyLabel: String): Money {
        return sumMoney(plannedExpenses.map { it.amount }, currencyLabel)
    }

    fun summarizeBudgetVsActual(planned: Money, actual: Money): BudgetVsActualSummary {
        val variance = actual - planned
        val utilizationPercent = if (planned.minorUnits == 0L) 0.0 else {
            (actual.toMajorUnits() / planned.toMajorUnits()) * 100.0
        }

        return BudgetVsActualSummary(
            planned = planned,
            actual = actual,
            variance = variance,
            utilizationPercent = utilizationPercent,
        )
    }

    fun summarizeProjectBudgetVsActual(
        project: ProjectPlan,
        items: List<ProjectItem>,
        currencyLabel: String,
    ): ProjectBudgetSummary {
        val budget = Money.fromMajorUnits(project.budget, currencyLabel)
        val actual = sumMoney(items.map { it.actual }, currencyLabel)
        val variance = actual - budget
        val utilizationPercent = if (budget.minorUnits == 0L) 0.0 else {
            (actual.toMajorUnits() / budget.toMajorUnits()) * 100.0
        }

        return ProjectBudgetSummary(
            project = project,
            budget = budget,
            actual = actual,
            variance = variance,
            budgetUtilizationPercent = utilizationPercent,
        )
    }

    private fun sumMoney(amounts: List<Double>, currencyLabel: String): Money {
        return amounts.fold(Money.zero(currencyLabel)) { acc, amount ->
            acc + Money.fromMajorUnits(amount, currencyLabel)
        }
    }
}

fun Double.toMoney(currencyLabel: String): Money {
    return Money.fromMajorUnits(this, currencyLabel)
}

private fun roundToMinorUnits(amount: Double): Long {
    val scaled = amount * CENTS_PER_UNIT
    return if (scaled >= 0.0) {
        floor(scaled + 0.5).toLong()
    } else {
        ceil(scaled - 0.5).toLong()
    }
}
