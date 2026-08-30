package br.inf.cepp.financemanager.repository

import androidx.room.*
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAll(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Long): Expense?

    @Query("SELECT * FROM expenses WHERE category_name = :categoryName ORDER BY date DESC")
    fun getByCategory(categoryName: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalSpentInRange(startDate: LocalDate, endDate: LocalDate): Flow<Double?>

    @Query("SELECT * FROM expenses WHERE status = :status ORDER BY date DESC")
    fun getByStatus(status: ExpenseStatus): Flow<List<Expense>>

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()
}
