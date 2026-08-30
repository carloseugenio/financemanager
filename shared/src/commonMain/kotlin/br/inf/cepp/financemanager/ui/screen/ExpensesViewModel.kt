package br.inf.cepp.financemanager.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.repository.IFinanceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ExpensesViewModel(private val financeService: IFinanceService) : ViewModel() {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    init {
        viewModelScope.launch {
            financeService.getAllExpenses().collect { _expenses.value = it }
        }
    }

    suspend fun refresh() {
        val list = financeService.getAllExpenses().first()
        _expenses.value = list
    }
}
