package br.inf.cepp.financemanager.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
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
@Entity(tableName = "finance_institutions")
data class FinanceInstitution(
    @PrimaryKey val name: String,
    val type: String,
)

/**
 * Accounts represents the source of money.
 */
@Serializable
@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey val name: String,
    val type: AccountType,
    @Embedded(prefix = "institution_")
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
 * Where the system got the expense from. It may be user manual entry,
 * CVS Import, reading user SMS entries or digital receipts
 */
@Serializable
enum class ExpenseSource {
    MANUAL, IMPORT, SMS, RECEIPT
}

@Serializable
enum class ExpenseStatus {
    DRAFT, CONFIRMED, PLANNED
}

// Recurrence rule for scheduled/recurring expenses
@Serializable
enum class RecurrenceFrequency {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

@Serializable
data class RecurrenceRule(
    val frequency: RecurrenceFrequency = RecurrenceFrequency.MONTHLY,
    val interval: Int = 1,               // e.g., every 1 month
    val count: Int? = null,              // optional number of occurrences
    @Serializable(with = SafeLocalDateSerializer::class)
    val until: LocalDate? = null         // optional end date
)

@Serializable
enum class ReminderMethod {
    NOTIFICATION, ALARM, SMS, EMAIL
}

@Serializable
data class Reminder(
    val enabled: Boolean = false,
    val method: ReminderMethod = ReminderMethod.NOTIFICATION,
    // leadTime in minutes before the scheduled date/time to trigger the reminder
    val leadTimeMinutes: Long = 60,
    // optional destination (phone number or email) for SMS/EMAIL reminders
    val destination: String? = null
)

/**
 * Tracks state for recurring expenses across worker runs.
 * Enables reliable scheduling: [nextRunDate] tells the worker when to next materialize,
 * [remainingCount] tracks occurrences left (null = infinite).
 */
@Serializable
@Entity(tableName = "recurring_expense_state", primaryKeys = ["sourceExpenseId"])
data class RecurringExpenseState(
    val sourceExpenseId: Long,  // FK to original PLANNED expense
    @Serializable(with = SafeLocalDateSerializer::class)
    val nextRunDate: LocalDate, // next date to materialize
    val remainingCount: Int? = null, // null = infinite, 0 = complete
    @Serializable(with = SafeLocalDateSerializer::class)
    val lastMaterializedDate: LocalDate? = null // for audit/debugging
)

@Serializable
@Entity(tableName = "expense_categories")
data class ExpenseCategory(
    @PrimaryKey val name: String,
    val color: HexColor,
    val iconKey: String
)

@Serializable
@Entity(tableName = "expense_items")
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    @Serializable(with = SafeLocalDateSerializer::class)
    val date: LocalDate,
    val amount: Double,
    val iconKey: String,
    val iconColor: HexColor,
    // optional recurrence and reminder information for scheduled/recurring expenses
    @Embedded(prefix = "rec_")
    val recurrence: RecurrenceRule? = null,
    @Embedded(prefix = "rem_")
    val reminder: Reminder? = null
)

/**
 * A Project plan is an intent to forecast expenses to achieve an objective.
 */
@Serializable
@Entity(tableName = "project_plans")
data class ProjectPlan(
    @PrimaryKey val name: String,
    @Serializable(with = SafeLocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = SafeLocalDateSerializer::class)
    val endDate: LocalDate,
    val budget: Double,
    val status: ProjectStatus = ProjectStatus.ACTIVE
)

@Serializable
enum class ProjectStatus {
    ACTIVE, FINISHED
}

@Serializable
@Entity(tableName = "project_items")
data class ProjectItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planName: String,
    @Embedded(prefix = "category_")
    val category: ExpenseCategory,
    val description: String,
    val budget: Double,
    val actual: Double,
    @Serializable(with = SafeLocalDateSerializer::class)
    val expectedDate: LocalDate,
    @Embedded(prefix = "related_")
    val relatedExpense: ExpenseItem
)

