package br.inf.cepp.financemanager.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
actual fun ExportScreen(viewModel: ExportViewModel, onBack: () -> Unit, initialType: String?) {
    val pendingRequest = remember { mutableStateOf<ExportRequest?>(null) }

    val csvPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        val request = pendingRequest.value
        pendingRequest.value = null
        if (uri != null && request != null) {
            when (request.exportType) {
                ExportType.Project -> when (request.format) {
                    ExportFormat.Csv -> viewModel.exportProjectsCsv(request.filename, request.selectedProject, uri.toString())
                    ExportFormat.Pdf -> viewModel.exportProjectsPdf(request.filename, request.selectedProject, uri.toString())
                }
                ExportType.Monthly -> when (request.format) {
                    ExportFormat.Csv -> viewModel.exportMonthlyCsv(request.filename, request.startDate, request.endDate, uri.toString())
                    ExportFormat.Pdf -> viewModel.exportMonthlyPdf(request.filename, request.startDate, request.endDate, uri.toString())
                }
            }
        } else {
            viewModel.exportCancelled()
        }
    }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        val request = pendingRequest.value
        pendingRequest.value = null
        if (uri != null && request != null) {
            when (request.exportType) {
                ExportType.Project -> when (request.format) {
                    ExportFormat.Csv -> viewModel.exportProjectsCsv(request.filename, request.selectedProject, uri.toString())
                    ExportFormat.Pdf -> viewModel.exportProjectsPdf(request.filename, request.selectedProject, uri.toString())
                }
                ExportType.Monthly -> when (request.format) {
                    ExportFormat.Csv -> viewModel.exportMonthlyCsv(request.filename, request.startDate, request.endDate, uri.toString())
                    ExportFormat.Pdf -> viewModel.exportMonthlyPdf(request.filename, request.startDate, request.endDate, uri.toString())
                }
            }
        } else {
            viewModel.exportCancelled()
        }
    }

    ExportScreenContent(
        viewModel = viewModel,
        onBack = onBack,
        initialType = initialType,
        onExportRequested = { request ->
            pendingRequest.value = request
            when (request.format) {
                ExportFormat.Csv -> csvPicker.launch(request.filename)
                ExportFormat.Pdf -> pdfPicker.launch(request.filename)
            }
        }
    )
}
