package br.inf.cepp.financemanager.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.repository.FinanceService
import br.inf.cepp.financemanager.util.today
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class DashboardViewModel(private val financeService: br.inf.cepp.financemanager.repository.IFinanceService) : ViewModel() {

    private val _viewData = MutableStateFlow<List<MonthlyExpensePerCategoryViewData>>(emptyList())
    val viewData: StateFlow<List<MonthlyExpensePerCategoryViewData>> = _viewData.asStateFlow()

    private val _totalSpentAmount = MutableStateFlow(0.0)
    val totalSpentAmount: StateFlow<Double> = _totalSpentAmount.asStateFlow()

    private val _transactionCount = MutableStateFlow(0)
    val transactionCount: StateFlow<Int> = _transactionCount.asStateFlow()

    private val _accountCount = MutableStateFlow(0)
    val accountCount: StateFlow<Int> = _accountCount.asStateFlow()

    private val _plannedExpenses = MutableStateFlow<List<ExpenseItem>>(emptyList())
    val plannedExpenses: StateFlow<List<ExpenseItem>> = _plannedExpenses.asStateFlow()

    private val _recentExpenses = MutableStateFlow<List<ExpenseItem>>(emptyList())
    val recentExpenses: StateFlow<List<ExpenseItem>> = _recentExpenses.asStateFlow()

    fun fetchData(month: kotlinx.datetime.Month = today().month) {
        viewModelScope.launch {
            financeService.getMonthlyExpensesData(month).collect {
                _viewData.value = it
            }
        }
        
        viewModelScope.launch {
            financeService.getTotalExpenses(month).collect {
                _totalSpentAmount.value = it
            }
        }

        viewModelScope.launch {
            financeService.getPlannedExpenses().collect {
                _plannedExpenses.value = it
            }
        }

        viewModelScope.launch {
            financeService.getAccounts().collect {
                _accountCount.value = it.size
            }
        }
        
        // Transaction count could be derived from expenses in month
        viewModelScope.launch {
            financeService.getAllExpenses().map { all ->
                all.filter { it.status == ExpenseStatus.CONFIRMED && it.date.month == month }.size
            }.collect {
                _transactionCount.value = it
            }
        }

        // Recent expenses: latest 5 confirmed entries
        viewModelScope.launch {
            financeService.getAllExpenses().map { all ->
                all.filter { it.status == ExpenseStatus.CONFIRMED }
                    .sortedByDescending { it.date }
                    .take(5)
                    .map { expense ->
                        ExpenseItem(
                            id = expense.id,
                            title = expense.description,
                            date = expense.date,
                            amount = expense.amount,
                            iconKey = expense.category.iconKey,
                            iconColor = expense.category.color
                        )
                    }
            }.collect {
                _recentExpenses.value = it
            }
        }
    }
}
