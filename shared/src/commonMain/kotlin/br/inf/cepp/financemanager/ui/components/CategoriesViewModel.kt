package br.inf.cepp.financemanager.ui.components

import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.repository.FinanceService
import br.inf.cepp.financemanager.ui.util.HexColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// ⚠️ CRUCIAL: Must use the multiplatform androidx lifecycle package!
import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class CategoriesViewModel(private val financeService: br.inf.cepp.financemanager.repository.IFinanceService) : ViewModel() {
    // 2. Keep the mutable version private so only this class can modify it
    private val _uiState = MutableStateFlow<ExpenseCategoriesUiState>(
        ExpenseCategoriesUiState.Loading)

    // 3. Expose a read-only StateFlow to the view layer
    val uiState: StateFlow<ExpenseCategoriesUiState> = _uiState.asStateFlow()

    fun fetchData() {
        viewModelScope.launch {
            _uiState.value = ExpenseCategoriesUiState.Loading
            financeService.getCategories().collect { items ->
                _uiState.value = ExpenseCategoriesUiState.Success(data = items)
            }
        }
    }

    fun createNewCategory(name: String, hexColor: HexColor, iconKey: String) {
        viewModelScope.launch {
            financeService.saveCategory(
                ExpenseCategory(name = name, color = hexColor, iconKey = iconKey)
            )
        }
    }

    fun updateCategory(category: ExpenseCategory) {
        viewModelScope.launch {
            financeService.updateCategory(category)
        }
    }

    fun deleteCategory(categoryName: String) {
        viewModelScope.launch {
            financeService.deleteCategory(categoryName)
        }
    }
}

sealed interface ExpenseCategoriesUiState {
    object Loading : ExpenseCategoriesUiState
    data class Success(val data: List<ExpenseCategory>) : ExpenseCategoriesUiState
    data class Error(val message: String) : ExpenseCategoriesUiState
}
