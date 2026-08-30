package br.inf.cepp.financemanager.repository

import androidx.room.*
import br.inf.cepp.financemanager.model.RecurringExpenseState
import kotlinx.datetime.LocalDate

@Dao
interface RecurringExpenseStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(state: RecurringExpenseState): Long

    @Update
    suspend fun update(state: RecurringExpenseState)

    @Delete
    suspend fun delete(state: RecurringExpenseState)

    @Query("SELECT * FROM recurring_expense_state WHERE sourceExpenseId = :sourceExpenseId")
    suspend fun getBySourceExpenseId(sourceExpenseId: Long): RecurringExpenseState?

    @Query("SELECT * FROM recurring_expense_state ORDER BY nextRunDate ASC")
    suspend fun getAll(): List<RecurringExpenseState>

    @Query("DELETE FROM recurring_expense_state")
    suspend fun deleteAll()
}
