package br.inf.cepp.financemanager.ui.components

import br.inf.cepp.financemanager.model.MonthlyExpensePerCategoryViewData
import br.inf.cepp.financemanager.repository.monthlyExpensesData
import br.inf.cepp.financemanager.util.today
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


// 1. Define your UI State Model
sealed interface UiState {
    object Loading : UiState
    data class Success(val data: List<MonthlyExpensePerCategoryViewData>) : UiState
    data class Error(val message: String) : UiState
}

class FinanceManagerViewModel {
    // 2. Keep the mutable version private so only this class can modify it
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)

    // 3. Expose a read-only StateFlow to the view layer
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun fetchData() {
        _uiState.value = UiState.Loading // Direct assignment

        // Simulating data fetch success...
        val items = monthlyExpensesData(today().month)
        //val items = listOf("Kotlin", "StateFlow", "Coroutines")

        // 4. Use .update for thread-safe/atomic state changes
        _uiState.update { UiState.Success(data = items) }
    }
}

