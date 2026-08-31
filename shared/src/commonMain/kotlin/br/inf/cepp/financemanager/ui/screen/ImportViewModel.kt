package br.inf.cepp.financemanager.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.inf.cepp.financemanager.domain.ImportIntelligenceCalculator
import br.inf.cepp.financemanager.domain.ImportReviewSummary
import br.inf.cepp.financemanager.model.ExpenseStatus
import br.inf.cepp.financemanager.repository.IFinanceService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ImportViewModel(private val financeService: IFinanceService) : ViewModel() {
    private val calculator = ImportIntelligenceCalculator()
    private var refreshJob: Job? = null

    private val _reviewSummary = MutableStateFlow<ImportReviewSummary?>(null)
    val reviewSummary: StateFlow<ImportReviewSummary?> = _reviewSummary.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            combine(
                financeService.getDraftExpenses(),
                financeService.getAllExpenses(),
                financeService.getCategories(),
            ) { drafts, allExpenses, categories ->
                val confirmedExpenses = allExpenses.filter { it.status == ExpenseStatus.CONFIRMED }
                calculator.summarizeDraftImports(drafts, confirmedExpenses, categories)
            }.collect { summary ->
                _reviewSummary.value = summary
            }
        }
    }
}
