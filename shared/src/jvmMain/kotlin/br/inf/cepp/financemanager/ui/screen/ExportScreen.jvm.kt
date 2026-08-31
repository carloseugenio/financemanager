package br.inf.cepp.financemanager.ui.screen

import androidx.compose.runtime.Composable

@Composable
actual fun ExportScreen(viewModel: ExportViewModel, onBack: () -> Unit, initialType: String?) {
    ExportScreenContent(
        viewModel = viewModel,
        onBack = onBack,
        initialType = initialType,
        onExportRequested = { request ->
            when (request.exportType) {
                ExportType.Project -> when (request.format) {
                    ExportFormat.Csv -> viewModel.exportProjectsCsv(request.filename, request.selectedProject)
                    ExportFormat.Pdf -> viewModel.exportProjectsPdf(request.filename, request.selectedProject)
                }
                ExportType.Monthly -> when (request.format) {
                    ExportFormat.Csv -> viewModel.exportMonthlyCsv(request.filename, request.startDate, request.endDate)
                    ExportFormat.Pdf -> viewModel.exportMonthlyPdf(request.filename, request.startDate, request.endDate)
                }
            }
        }
    )
}
