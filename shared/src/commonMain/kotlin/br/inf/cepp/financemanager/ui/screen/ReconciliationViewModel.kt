package br.inf.cepp.financemanager.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.repository.IFinanceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ReconciliationViewModel(private val financeService: IFinanceService) : ViewModel() {
    private val _draftExpenses = MutableStateFlow<List<Expense>>(emptyList())
    val draftExpenses: StateFlow<List<Expense>> = _draftExpenses.asStateFlow()

    init {
        refreshDrafts()
    }

    fun refreshDrafts() {
        viewModelScope.launch {
            financeService.getDraftExpenses().collect { drafts ->
                _draftExpenses.value = drafts
            }
        }
    }

    fun confirmDraft(expenseId: Long) {
        viewModelScope.launch {
            financeService.confirmExpense(expenseId)
        }
    }

    fun confirmAll() {
        viewModelScope.launch {
            financeService.confirmAllDrafts()
        }
    }
}
