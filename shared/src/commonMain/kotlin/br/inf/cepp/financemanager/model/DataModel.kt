package br.inf.cepp.financemanager.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import br.inf.cepp.financemanager.ui.util.HexColor
import br.inf.cepp.financemanager.util.SafeLocalDateSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * A UI only representation of expenses for one month for a [ExpenseCategory].
 * This is a VIEW ONLY DTO and will not be subject to JSON parser or
 * database/ORM conversions.
 */
data class MonthlyExpensePerCategoryViewData(
    val name: String,
    val amount: Double,
    val percentage: Double,
    val color: Color,
    val icon: ImageVector
)

@Serializable
data class FinanceInstitution(
    val name: String,
    val type: String,
)

/**
 * Accounts represents the source of money.
 */
@Serializable
data class Account(
    val name: String,
    val type: AccountType,
    val financeInstitution: FinanceInstitution,
    val branch: String,
    val number: String,
    val network: String,
    val limit: Double,
    @Serializable(with = SafeLocalDateSerializer::class)
    val monthlyDueDate: Int,
    val balance: Double,
    val color: HexColor,
    val iconKey: String,
)

@Serializable
enum class AccountType {
    CHECKING, SAVINGS, CREDIT, DIGITAL
}

@Serializable
data class Record(
    val description: String,
    val type: RecordType,
    val expense: Expense,
    val income: Income,
)

@Serializable
enum class RecordType {
    EXPENSE, INCOME
}

@Serializable
data class Income(
    val category: IncomeCategory,
    @Serializable(with = SafeLocalDateSerializer::class)
    val date: LocalDate,
)

@Serializable
enum class IncomeCategory {
    SALARY, RENTAL, DIVIDEND
}

/**
 * Record of an Expense in the system. Expenses can be [ExpenseStatus.CONFIRMED] or
 * [ExpenseStatus.PLANNED].
 */
@Serializable
data class Expense(
    val category: ExpenseCategory,
    @Serializable(with = SafeLocalDateSerializer::class)
    val date: LocalDate,
    val amount: Double,
    val status: ExpenseStatus,
    val source: ExpenseSource
)

/**
 * Where the system got the expense from. It may be user manual entry,
 * CVS Import, reading user SMS entries or digital receipts
 */
@Serializable
enum class ExpenseSource {
    MANUAL, IMPORT, SMS, RECEIPT
}

@Serializable
enum class ExpenseStatus {
    CONFIRMED, PLANNED
}

@Serializable
data class ExpenseCategory(
    val name: String,
    val color: HexColor,
    val iconKey: String
)

@Serializable
data class ExpenseItem(
    val title: String,
    @Serializable(with = SafeLocalDateSerializer::class)
    val date: LocalDate,
    val amount: Double,
    val iconKey: String,
    val iconColor: HexColor
)

/**
 * A Project plan is an intent to forecast expenses to achieve an objective.
 */
@Serializable
data class ProjectPlan(
    val name: String,
    val period: String,
    val budget: Double,
    val status: ProjectStatus = ProjectStatus.ACTIVE,
    val items: List<ProjectItem>
)

@Serializable
enum class ProjectStatus {
    ACTIVE, FINISHED
}

@Serializable
data class ProjectItem(
    val category: ExpenseCategory,
    val description: String,
    val budget: Double,
    val actual: Double,
    @Serializable(with = SafeLocalDateSerializer::class)
    val expectedDate: LocalDate,
    val relatedExpense: ExpenseItem
)

