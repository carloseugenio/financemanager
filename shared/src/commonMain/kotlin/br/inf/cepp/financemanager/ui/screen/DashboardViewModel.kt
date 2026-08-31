package br.inf.cepp.financemanager.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.inf.cepp.financemanager.domain.BudgetVsActualSummary
import br.inf.cepp.financemanager.domain.CashflowSummary
import br.inf.cepp.financemanager.domain.FinancialSummaryCalculator
import br.inf.cepp.financemanager.domain.Money
import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.repository.FinanceService
import br.inf.cepp.financemanager.util.today
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Month
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class DashboardViewModel(private val financeService: br.inf.cepp.financemanager.repository.IFinanceService) : ViewModel() {
    private val financialSummaryCalculator = FinancialSummaryCalculator()

    private val _viewData = MutableStateFlow<List<MonthlyExpensePerCategoryViewData>>(emptyList())
    val viewData: StateFlow<List<MonthlyExpensePerCategoryViewData>> = _viewData.asStateFlow()

    private val _totalSpentAmount = MutableStateFlow(0.0)
    val totalSpentAmount: StateFlow<Double> = _totalSpentAmount.asStateFlow()

    private val _transactionCount = MutableStateFlow(0)
    val transactionCount: StateFlow<Int> = _transactionCount.asStateFlow()

    private val _accountCount = MutableStateFlow(0)
    val accountCount: StateFlow<Int> = _accountCount.asStateFlow()

    private val _previousMonthSpentAmount = MutableStateFlow(0.0)
    val previousMonthSpentAmount: StateFlow<Double> = _previousMonthSpentAmount.asStateFlow()

    private val _plannedBudgetAmount = MutableStateFlow(0.0)
    val plannedBudgetAmount: StateFlow<Double> = _plannedBudgetAmount.asStateFlow()

    private val _budgetVsActual = MutableStateFlow<BudgetVsActualSummary?>(null)
    val budgetVsActual: StateFlow<BudgetVsActualSummary?> = _budgetVsActual.asStateFlow()

    private val _plannedExpenses = MutableStateFlow<List<ExpenseItem>>(emptyList())
    val plannedExpenses: StateFlow<List<ExpenseItem>> = _plannedExpenses.asStateFlow()

    private val _plannedIncomes = MutableStateFlow<List<Income>>(emptyList())
    val plannedIncomes: StateFlow<List<Income>> = _plannedIncomes.asStateFlow()

    private val _cashflowSummary = MutableStateFlow<CashflowSummary?>(null)
    val cashflowSummary: StateFlow<CashflowSummary?> = _cashflowSummary.asStateFlow()

    private val _recentExpenses = MutableStateFlow<List<ExpenseItem>>(emptyList())
    val recentExpenses: StateFlow<List<ExpenseItem>> = _recentExpenses.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var loadedMonthlyData = false
    private var loadedTotalSpent = false
    private var loadedPlannedExpenses = false
    private var loadedPlannedIncomes = false
    private var loadedCashflowSummary = false
    private var loadedPreviousMonth = false
    private var loadedAccounts = false
    private var loadedExpenseList = false

    fun fetchData(month: kotlinx.datetime.Month = today().month) {
        val previous = previousMonth(month)
        resetLoadingState()

        viewModelScope.launch {
            try {
                financeService.getMonthlyExpensesData(month).collect {
                    _viewData.value = it
                    loadedMonthlyData = true
                    updateLoadingState()
                }
            } catch (error: Throwable) {
                reportError(error)
            }
        }
        
        viewModelScope.launch {
            try {
                financeService.getTotalExpenses(month).collect {
                    _totalSpentAmount.value = it
                    updateBudgetSummary()
                    loadedTotalSpent = true
                    updateLoadingState()
                }
            } catch (error: Throwable) {
                reportError(error)
            }
        }

        viewModelScope.launch {
            try {
                financeService.getPlannedExpenses().collect {
                    _plannedExpenses.value = it
                    _plannedBudgetAmount.value = financialSummaryCalculator.summarizePlannedExpenses(it, "unknown").toMajorUnits()
                    updateBudgetSummary()
                    loadedPlannedExpenses = true
                    updateLoadingState()
                }
            } catch (error: Throwable) {
                reportError(error)
            }
        }

        viewModelScope.launch {
            try {
                financeService.getPlannedIncomes().collect {
                    _plannedIncomes.value = it
                    loadedPlannedIncomes = true
                    updateLoadingState()
                }
            } catch (error: Throwable) {
                reportError(error)
            }
        }

        viewModelScope.launch {
            try {
                financeService.getMonthlyCashflow(month).collect {
                    _cashflowSummary.value = it
                    loadedCashflowSummary = true
                    updateLoadingState()
                }
            } catch (error: Throwable) {
                reportError(error)
            }
        }

        viewModelScope.launch {
            try {
                financeService.getTotalExpenses(previous).collect {
                    _previousMonthSpentAmount.value = it
                    loadedPreviousMonth = true
                    updateLoadingState()
                }
            } catch (error: Throwable) {
                reportError(error)
            }
        }

        viewModelScope.launch {
            try {
                financeService.getAccounts().collect {
                    _accountCount.value = it.size
                    loadedAccounts = true
                    updateLoadingState()
                }
            } catch (error: Throwable) {
                reportError(error)
            }
        }

        viewModelScope.launch {
            try {
                financeService.getAllExpenses().collect { all ->
                    _transactionCount.value = all.filter { it.status == ExpenseStatus.CONFIRMED && it.date.month == month }.size
                    _recentExpenses.value = all.filter { it.status == ExpenseStatus.CONFIRMED }
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
                    loadedExpenseList = true
                    updateLoadingState()
                }
            } catch (error: Throwable) {
                reportError(error)
            }
        }
    }

    private fun resetLoadingState() {
        loadedMonthlyData = false
        loadedTotalSpent = false
        loadedPlannedExpenses = false
        loadedPlannedIncomes = false
        loadedCashflowSummary = false
        loadedPreviousMonth = false
        loadedAccounts = false
        loadedExpenseList = false
        _errorMessage.value = null
        _isLoading.value = true
    }

    private fun updateLoadingState() {
        if (
            loadedMonthlyData &&
            loadedTotalSpent &&
            loadedPlannedExpenses &&
            loadedPlannedIncomes &&
            loadedCashflowSummary &&
            loadedPreviousMonth &&
            loadedAccounts &&
            loadedExpenseList
        ) {
            _isLoading.value = false
        }
    }

    private fun reportError(error: Throwable) {
        _errorMessage.value = error.message ?: "Failed to load dashboard data"
        _isLoading.value = false
    }

    private fun previousMonth(month: Month): Month = when (month) {
        Month.JANUARY -> Month.DECEMBER
        else -> Month.values()[month.ordinal - 1]
    }

    private fun updateBudgetSummary() {
        val planned = Money.fromMajorUnits(_plannedBudgetAmount.value, "unknown")
        val actual = Money.fromMajorUnits(_totalSpentAmount.value, "unknown")
        _budgetVsActual.value = financialSummaryCalculator.summarizeBudgetVsActual(planned, actual)
    }
}
