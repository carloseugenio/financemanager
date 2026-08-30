package br.inf.cepp.financemanager.ui.components

import br.inf.cepp.financemanager.model.MonthlyExpensePerCategoryViewData
import br.inf.cepp.financemanager.repository.FinanceService
import br.inf.cepp.financemanager.util.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// 1. Define your UI State Model
sealed interface UiState {
    object Loading : UiState
    data class Success(val data: List<MonthlyExpensePerCategoryViewData>) : UiState
    data class Error(val message: String) : UiState
}

class FinanceManagerViewModel(private val financeService: br.inf.cepp.financemanager.repository.IFinanceService) : ViewModel() {
    // 2. Keep the mutable version private so only this class can modify it
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)

    // 3. Expose a read-only StateFlow to the view layer
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun fetchData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            financeService.getMonthlyExpensesData(today().month).collect { items ->
                _uiState.value = UiState.Success(data = items)
            }
        }
    }
}

