package br.inf.cepp.financemanager.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.repository.FinanceService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class AddExpenseViewModel(private val financeService: br.inf.cepp.financemanager.repository.IFinanceService) : ViewModel() {

    private val _categories = MutableStateFlow<List<ExpenseCategory>>(emptyList())
    val categories: StateFlow<List<ExpenseCategory>> = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            financeService.getCategories().collect {
                _categories.value = it
            }
        }
    }

    fun saveExpense(
        description: String,
        amount: Double,
        category: ExpenseCategory,
        date: kotlinx.datetime.LocalDate,
        status: ExpenseStatus,
        source: ExpenseSource,
        recurrence: RecurrenceRule?,
        reminder: Reminder?
    ) {
        viewModelScope.launch {
            financeService.saveExpense(
                Expense(
                    description = description,
                    category = category,
                    date = date,
                    amount = amount,
                    status = status,
                    source = source,
                    recurrence = recurrence,
                    reminder = reminder
                )
            )
        }
    }
}
