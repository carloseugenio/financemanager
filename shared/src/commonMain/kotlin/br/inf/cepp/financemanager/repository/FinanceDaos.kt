package br.inf.cepp.financemanager.repository

import androidx.room.*
import br.inf.cepp.financemanager.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account)

    @Query("SELECT * FROM accounts")
    fun getAll(): Flow<List<Account>>
}

@Dao
interface FinanceInstitutionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(institution: FinanceInstitution)

    @Query("SELECT * FROM finance_institutions")
    fun getAll(): Flow<List<FinanceInstitution>>
}

@Dao
interface ExpenseCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: ExpenseCategory)

    @Update
    suspend fun update(category: ExpenseCategory)

    @Delete
    suspend fun delete(category: ExpenseCategory)

    @Query("DELETE FROM expense_categories WHERE name = :categoryName")
    suspend fun deleteByName(categoryName: String)

    @Query("SELECT * FROM expense_categories")
    fun getAll(): Flow<List<ExpenseCategory>>
}

@Dao
interface ExpenseItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ExpenseItem)

    @Query("SELECT * FROM expense_items ORDER BY date DESC")
    fun getAll(): Flow<List<ExpenseItem>>
}

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(income: Income): Long

    @Update
    suspend fun update(income: Income)

    @Query("SELECT * FROM incomes ORDER BY date DESC")
    fun getAll(): Flow<List<Income>>

    @Query("SELECT * FROM incomes WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Income>>

    @Query("SELECT * FROM incomes WHERE category = :category ORDER BY date DESC")
    fun getByCategory(category: IncomeCategory): Flow<List<Income>>

    @Query("SELECT * FROM incomes WHERE status = :status ORDER BY date DESC")
    fun getByStatus(status: ExpenseStatus): Flow<List<Income>>
}

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: ProjectPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ProjectItem)

    @Delete
    suspend fun deleteItem(item: ProjectItem)

    @Query("SELECT * FROM project_plans")
    fun getAllPlans(): Flow<List<ProjectPlan>>

    @Query("SELECT * FROM project_items WHERE planName = :planName")
    fun getItemsForPlan(planName: String): Flow<List<ProjectItem>>
}
