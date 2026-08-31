package br.inf.cepp.financemanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.repository.*

@Database(
    entities = [
        Person::class,
        Expense::class,
        Income::class,
        Account::class,
        ExpenseCategory::class,
        ExpenseItem::class,
        ProjectPlan::class,
        ProjectItem::class,
        FinanceInstitution::class,
        RecurringExpenseState::class,
    ],
    version = 6
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun accountDao(): AccountDao
    abstract fun institutionDao(): FinanceInstitutionDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao
    abstract fun expenseItemDao(): ExpenseItemDao
    abstract fun projectDao(): ProjectDao
    abstract fun recurringExpenseStateDao(): RecurringExpenseStateDao
}
