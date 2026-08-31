package br.inf.cepp.financemanager.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.inf.cepp.financemanager.model.ProjectItem
import br.inf.cepp.financemanager.repository.IFinanceService
import br.inf.cepp.financemanager.util.exportMonthlyToCsv
import br.inf.cepp.financemanager.util.exportProjectsToCsv
import br.inf.cepp.financemanager.util.saveToFile
import br.inf.cepp.financemanager.util.sanitizeFilename
import br.inf.cepp.financemanager.util.shareFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.annotation.KoinViewModel

data class ExportHistoryItem(
    val path: String,
    val mimeType: String,
)

@KoinViewModel
class ExportViewModel(
    private val financeService: IFinanceService,
    private val fileSaver: (String, ByteArray, String?) -> String = ::saveToFile,
    private val fileSharer: (String, String) -> String = ::shareFile,
) : ViewModel() {
    private val _lastExportStatus = MutableStateFlow<String?>(null)
    val lastExportStatus: StateFlow<String?> = _lastExportStatus.asStateFlow()

    private val _projects = MutableStateFlow<List<br.inf.cepp.financemanager.model.ProjectPlan>>(emptyList())
    val projects: StateFlow<List<br.inf.cepp.financemanager.model.ProjectPlan>> = _projects.asStateFlow()

    private val _history = MutableStateFlow<List<ExportHistoryItem>>(emptyList())
    val history: StateFlow<List<ExportHistoryItem>> = _history.asStateFlow()

    init {
        viewModelScope.launch {
            financeService.getProjectPlans().collect { _projects.value = it }
        }
    }

    fun exportProjectsCsv(filename: String, projectName: String? = null, destination: String? = null) {
        viewModelScope.launch {
            try {
                val plans = financeService.getProjectPlans().first()
                val filtered = projectName?.let { name -> plans.filter { it.name == name } } ?: plans
                val items = loadProjectItems(filtered)
                val safeName = sanitizeFilename(ensureExtension(filename, "csv"))
                saveExport(safeName, exportProjectsToCsv(filtered, items), "text/csv", destination, "CSV")
            } catch (error: Throwable) {
                _lastExportStatus.value = "Export failed: ${error.message}"
            }
        }
    }

    fun exportMonthlyCsv(filename: String, start: LocalDate? = null, end: LocalDate? = null, destination: String? = null) {
        viewModelScope.launch {
            try {
                val expenses = financeService.getAllExpenses().first()
                val filtered = if (start != null && end != null) {
                    expenses.filter { expense -> expense.date >= start && expense.date <= end }
                } else {
                    expenses
                }
                val safeName = sanitizeFilename(ensureExtension(filename, "csv"))
                saveExport(safeName, exportMonthlyToCsv(filtered), "text/csv", destination, "CSV")
            } catch (error: Throwable) {
                _lastExportStatus.value = "Export failed: ${error.message}"
            }
        }
    }

    fun exportProjectsPdf(filename: String, projectName: String? = null, destination: String? = null) {
        viewModelScope.launch {
            try {
                val plans = financeService.getProjectPlans().first()
                val filtered = projectName?.let { name -> plans.filter { it.name == name } } ?: plans
                val items = loadProjectItems(filtered)
                val pdfBytes = br.inf.cepp.financemanager.pdf.generateProjectsPdf(filtered, items)
                val safeName = sanitizeFilename(ensureExtension(filename, "pdf"))
                saveExport(safeName, pdfBytes, "application/pdf", destination, "PDF")
            } catch (error: Throwable) {
                _lastExportStatus.value = "PDF export failed: ${error.message}"
            }
        }
    }

    fun exportMonthlyPdf(filename: String, start: LocalDate? = null, end: LocalDate? = null, destination: String? = null) {
        viewModelScope.launch {
            try {
                val expenses = financeService.getAllExpenses().first()
                val filtered = if (start != null && end != null) {
                    expenses.filter { expense -> expense.date >= start && expense.date <= end }
                } else {
                    expenses
                }
                val safeName = sanitizeFilename(ensureExtension(filename, "pdf"))
                val pdfBytes = br.inf.cepp.financemanager.pdf.generateMonthlyPdf(filtered)
                saveExport(safeName, pdfBytes, "application/pdf", destination, "PDF")
            } catch (error: Throwable) {
                _lastExportStatus.value = "PDF export failed: ${error.message}"
            }
        }
    }

    fun shareFilePath(path: String, mimeType: String = "application/octet-stream") {
        viewModelScope.launch {
            try {
                val result = fileSharer(path, mimeType)
                if (result.isErrorResult()) {
                    _lastExportStatus.value = "Share failed: ${result.errorMessage()}"
                } else {
                    _lastExportStatus.value = "Shared successfully"
                }
            } catch (error: Throwable) {
                _lastExportStatus.value = "Share failed: ${error.message}"
            }
        }
    }

    private suspend fun loadProjectItems(projects: List<br.inf.cepp.financemanager.model.ProjectPlan>): Map<String, List<ProjectItem>> {
        return projects.associate { project ->
            project.name to financeService.getProjectItems(project.name).first()
        }
    }

    private fun ensureExtension(filename: String, extension: String): String {
        return if (filename.endsWith(".$extension", ignoreCase = true)) filename else "$filename.$extension"
    }

    fun exportCancelled() {
        _lastExportStatus.value = "Export cancelled"
    }

    private fun saveExport(filename: String, content: String, mimeType: String, destination: String?, label: String) {
        val result = fileSaver(filename, content.encodeToByteArray(), destination)
        if (result.isBlank()) {
            _lastExportStatus.value = "Export cancelled"
            return
        }
        if (result.isErrorResult()) {
            _lastExportStatus.value = "$label export failed: ${result.errorMessage()}"
            return
        }
        _history.value = listOf(ExportHistoryItem(path = result, mimeType = mimeType)) + _history.value.take(9)
        _lastExportStatus.value = "$label saved: $result"
    }

    private fun saveExport(filename: String, content: ByteArray, mimeType: String, destination: String?, label: String) {
        val result = fileSaver(filename, content, destination)
        if (result.isBlank()) {
            _lastExportStatus.value = "Export cancelled"
            return
        }
        if (result.isErrorResult()) {
            _lastExportStatus.value = "$label export failed: ${result.errorMessage()}"
            return
        }
        _history.value = listOf(ExportHistoryItem(path = result, mimeType = mimeType)) + _history.value.take(9)
        _lastExportStatus.value = "$label saved: $result"
    }

    private fun String.isErrorResult(): Boolean = startsWith("error:", ignoreCase = true)

    private fun String.errorMessage(): String = substringAfter("error:", missingDelimiterValue = trim()).trim().ifBlank { "unknown error" }
}
