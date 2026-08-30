package br.inf.cepp.financemanager.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.inf.cepp.financemanager.util.SafeLocalDateSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Record of an Expense in the system. Expenses can be [ExpenseStatus.DRAFT],
 * [ExpenseStatus.CONFIRMED], or [ExpenseStatus.PLANNED].
 */
@Serializable
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String = "",
    @Embedded(prefix = "category_")
    val category: ExpenseCategory,
    @Serializable(with = SafeLocalDateSerializer::class)
    val date: LocalDate,
    val amount: Double,
    val status: ExpenseStatus,
    val source: ExpenseSource,
    @Embedded(prefix = "rec_")
    val recurrence: RecurrenceRule? = null,
    @Embedded(prefix = "rem_")
    val reminder: Reminder? = null
)
