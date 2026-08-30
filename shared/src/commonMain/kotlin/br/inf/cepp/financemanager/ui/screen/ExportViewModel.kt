package br.inf.cepp.financemanager.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import br.inf.cepp.financemanager.util.exportProjectsToCsv
import br.inf.cepp.financemanager.util.exportMonthlyToCsv
import br.inf.cepp.financemanager.util.saveToFile
import kotlinx.coroutines.flow.first

@KoinViewModel
class ExportViewModel(private val financeService: br.inf.cepp.financemanager.repository.IFinanceService) : ViewModel() {
    private val _lastExportStatus = MutableStateFlow<String?>(null)
    val lastExportStatus: StateFlow<String?> = _lastExportStatus.asStateFlow()

    private val _projects = MutableStateFlow<List<br.inf.cepp.financemanager.model.ProjectPlan>>(emptyList())
    val projects: StateFlow<List<br.inf.cepp.financemanager.model.ProjectPlan>> = _projects.asStateFlow()

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> = _history.asStateFlow()

    init {
        viewModelScope.launch {
            financeService.getProjectPlans().collect { _projects.value = it }
        }
    }

    fun exportProjectsCsv(filename: String, projectName: String? = null) {
        viewModelScope.launch {
            try {
                val plans = financeService.getProjectPlans().first()
                val filtered = projectName?.let { name -> plans.filter { it.name == name } } ?: plans
                val itemMap = mutableMapOf<String, List<br.inf.cepp.financemanager.model.ProjectItem>>()
                filtered.forEach { p ->
                    itemMap[p.name] = financeService.getProjectItems(p.name).first()
                }
                val csv = exportProjectsToCsv(filtered, itemMap)
                                val safeName = br.inf.cepp.financemanager.util.sanitizeFilename(filename)
                                val path = saveToFile(safeName, csv.toByteArray())
                                _lastExportStatus.value = "CSV saved: $path"
                                _history.value = listOf(path) + _history.value.take(9)
                            } catch (t: Throwable) {
                                _lastExportStatus.value = "Export failed: ${t.message}"
                            }
        }
    }

    fun exportMonthlyCsv(filename: String, start: kotlinx.datetime.LocalDate? = null, end: kotlinx.datetime.LocalDate? = null) {
        viewModelScope.launch {
            try {
                val expenses = financeService.getAllExpenses().first()
                val filtered = if (start != null && end != null) {
                    expenses.filter { e -> !(e.date < start) && !(e.date > end) }
                } else expenses
                val csv = exportMonthlyToCsv(filtered)
                                val safeName = br.inf.cepp.financemanager.util.sanitizeFilename(filename)
                                val path = saveToFile(safeName, csv.toByteArray())
                                _lastExportStatus.value = "CSV saved: $path"
                                _history.value = listOf(path) + _history.value.take(9)
                            } catch (t: Throwable) {
                                _lastExportStatus.value = "Export failed: ${t.message}"
                            }
        }
    }

    // Hook for PDF generation on platforms where implemented
    fun exportProjectsPdf(filename: String, projectName: String? = null) {
        viewModelScope.launch {
            try {
                val plans = financeService.getProjectPlans().first()
                val filtered = projectName?.let { name -> plans.filter { it.name == name } } ?: plans
                val itemMap = mutableMapOf<String, List<br.inf.cepp.financemanager.model.ProjectItem>>()
                filtered.forEach { p ->
                    itemMap[p.name] = financeService.getProjectItems(p.name).first()
                }
                val pdfBytes = br.inf.cepp.financemanager.pdf.generateProjectsPdf(filtered, itemMap)
                                val safeName = br.inf.cepp.financemanager.util.sanitizeFilename(filename)
                                val path = saveToFile(safeName, pdfBytes)
                                _lastExportStatus.value = "PDF saved: $path"
                                _history.value = listOf(path) + _history.value.take(9)
                            } catch (t: Throwable) {
                                _lastExportStatus.value = "PDF export failed: ${t.message}"
                            }
        }
    }

    fun exportMonthlyPdf(filename: String, start: kotlinx.datetime.LocalDate? = null, end: kotlinx.datetime.LocalDate? = null) {
        viewModelScope.launch {
            try {
                val expenses = financeService.getAllExpenses().first()
                val filtered = if (start != null && end != null) {
                    expenses.filter { e -> !(e.date < start) && !(e.date > end) }
                } else expenses
                val pdfBytes = br.inf.cepp.financemanager.pdf.generateMonthlyPdf(filtered)
                                val safeName = br.inf.cepp.financemanager.util.sanitizeFilename(filename)
                                val path = saveToFile(safeName, pdfBytes)
                                _lastExportStatus.value = "PDF saved: $path"
                                _history.value = listOf(path) + _history.value.take(9)
                            } catch (t: Throwable) {
                                _lastExportStatus.value = "PDF export failed: ${t.message}"
                            }
        }
    }

    // Platform-agnostic hook to share a file (Android will invoke share intent)
    fun shareFilePath(path: String, mimeType: String = "application/octet-stream") {
        viewModelScope.launch {
            try {
                val res = br.inf.cepp.financemanager.util.shareFile(path, mimeType)
                _lastExportStatus.value = "Share result: $res"
            } catch (t: Throwable) {
                _lastExportStatus.value = "Share failed: ${t.message}"
            }
        }
    }
}

