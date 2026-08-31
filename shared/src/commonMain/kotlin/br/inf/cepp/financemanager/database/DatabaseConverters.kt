package br.inf.cepp.financemanager.database

import androidx.room.TypeConverter
import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.ui.util.HexColor
import kotlinx.datetime.LocalDate

class DatabaseConverters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromExpenseStatus(value: ExpenseStatus?): String? = value?.name

    @TypeConverter
    fun toExpenseStatus(value: String?): ExpenseStatus? = value?.let { ExpenseStatus.valueOf(it) }

    @TypeConverter
    fun fromExpenseSource(value: ExpenseSource?): String? = value?.name

    @TypeConverter
    fun toExpenseSource(value: String?): ExpenseSource? = value?.let { ExpenseSource.valueOf(it) }

    @TypeConverter
    fun fromIncomeCategory(value: IncomeCategory?): String? = value?.name

    @TypeConverter
    fun toIncomeCategory(value: String?): IncomeCategory? = value?.let { IncomeCategory.valueOf(it) }

    @TypeConverter
    fun fromHexColor(value: HexColor?): String? = value?.hex

    @TypeConverter
    fun toHexColor(value: String?): HexColor? = value?.let { HexColor(it) }

    @TypeConverter
    fun fromAccountType(value: AccountType?): String? = value?.name

    @TypeConverter
    fun toAccountType(value: String?): AccountType? = value?.let { AccountType.valueOf(it) }

    @TypeConverter
    fun fromProjectStatus(value: ProjectStatus?): String? = value?.name

    @TypeConverter
    fun toProjectStatus(value: String?): ProjectStatus? = value?.let { ProjectStatus.valueOf(it) }
}
