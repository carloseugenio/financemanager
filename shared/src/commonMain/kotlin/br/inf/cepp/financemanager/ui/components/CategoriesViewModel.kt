package br.inf.cepp.financemanager.ui.components

import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.repository.allCategories
import br.inf.cepp.financemanager.ui.util.HexColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ⚠️ CRUCIAL: Must use the multiplatform androidx lifecycle package!
import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class CategoriesViewModel : ViewModel() {
    // 2. Keep the mutable version private so only this class can modify it
    private val _uiState = MutableStateFlow<ExpenseCategoriesUiState>(
        ExpenseCategoriesUiState.Loading)

    // 3. Expose a read-only StateFlow to the view layer
    val uiState: StateFlow<ExpenseCategoriesUiState> = _uiState.asStateFlow()

    fun fetchData() {
        _uiState.value = ExpenseCategoriesUiState.Loading // Direct assignment

        // Simulating data fetch success...
        val items = allCategories
        //val items = listOf("Kotlin", "StateFlow", "Coroutines")

        // 4. Use .update for thread-safe/atomic state changes
        _uiState.update { ExpenseCategoriesUiState.Success(data = items) }
    }

    fun createNewCategory(name: String, hexColor: HexColor, iconKey: String) {
        allCategories.add(
            ExpenseCategory(name = name, color = hexColor, iconKey = iconKey)
        )
    }
}

sealed interface ExpenseCategoriesUiState {
    object Loading : ExpenseCategoriesUiState
    data class Success(val data: List<ExpenseCategory>) : ExpenseCategoriesUiState
    data class Error(val message: String) : ExpenseCategoriesUiState
}
