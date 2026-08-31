package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

data class ExportRequest(
    val format: ExportFormat,
    val exportType: ExportType,
    val filename: String,
    val selectedProject: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val monthlyStartDate: LocalDate?,
    val monthlyEndDate: LocalDate?,
)

enum class ExportType(val label: String) {
    Project("Project"),
    Monthly("Monthly statement"),
}

enum class ExportFormat(val extension: String, val mimeType: String) {
    Csv("csv", "text/csv"),
    Pdf("pdf", "application/pdf"),
}

@Composable
expect fun ExportScreen(viewModel: ExportViewModel, onBack: () -> Unit, initialType: String? = null)

@Composable
internal fun ExportScreenContent(
    viewModel: ExportViewModel,
    onBack: () -> Unit,
    initialType: String? = null,
    onExportRequested: (ExportRequest) -> Unit,
) {
    val status by viewModel.lastExportStatus.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val history by viewModel.history.collectAsState()
    val resolvedInitialType = initialType?.trim()?.takeIf { it.isNotEmpty() }
    var exportType by remember(resolvedInitialType) {
        mutableStateOf(
            resolvedInitialType?.let { value ->
                runCatching { ExportType.valueOf(value) }.getOrNull() ?: ExportType.Project
            } ?: ExportType.Project
        )
    }
    var selectedProject by remember { mutableStateOf<String?>(null) }
    val today = br.inf.cepp.financemanager.util.today()
    var startText by remember { mutableStateOf(today.toString()) }
    var endText by remember { mutableStateOf(today.toString()) }
    var monthText by remember { mutableStateOf("%04d-%02d".format(today.year, today.month.ordinal + 1)) }
    var filename by remember { mutableStateOf("") }
    var typeMenuOpen by remember { mutableStateOf(false) }
    var projectMenuOpen by remember { mutableStateOf(false) }
    var startError by remember { mutableStateOf(false) }
    var endError by remember { mutableStateOf(false) }

    fun defaultFilename(extension: String): String {
        val today = br.inf.cepp.financemanager.util.today()
        val timestamp = "%04d%02d%02d_%02d%02d".format(today.year, today.month.ordinal + 1, today.dayOfMonth, (Math.random() * 60).toInt(), (Math.random() * 60).toInt())
        return when (exportType) {
            ExportType.Project -> "${selectedProject ?: "projects"}_export_$timestamp.$extension"
            ExportType.Monthly -> "monthly_statement_$timestamp.$extension"
        }
    }

    val startDate = parseDate(startText)
    val endDate = parseDate(endText)
    val monthlyStartDate = parseMonth(monthText)
    val monthlyEndDate = monthlyStartDate?.let { date ->
        val monthLength = when (date.month) {
            Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY, Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            Month.FEBRUARY -> if (isLeapYear(date.year)) 29 else 28
        }
        LocalDate(date.year, date.month, monthLength)
    }
    val projectRangeValid = startDate != null && endDate != null && startDate <= endDate
    val monthlyRangeValid = monthlyStartDate != null && monthlyEndDate != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(padding),
            color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Export data", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
                Text("Generate CSV or PDF files for projects or monthly statements.", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)

                OutlinedTextField(
                    value = exportType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Export type") },
                    trailingIcon = { TextButton(onClick = { typeMenuOpen = true }) { Text("Change") } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = financeOutlinedTextFieldColors()
                )
                DropdownMenu(expanded = typeMenuOpen, onDismissRequest = { typeMenuOpen = false }) {
                    ExportType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.label) },
                            onClick = {
                                exportType = type
                                filename = ""
                                typeMenuOpen = false
                            }
                        )
                    }
                }

                if (exportType == ExportType.Project) {
                    OutlinedTextField(
                        value = selectedProject ?: "All projects",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Project") },
                        trailingIcon = { TextButton(onClick = { projectMenuOpen = true }) { Text("Select") } },
                        modifier = Modifier.fillMaxWidth(),
                        colors = financeOutlinedTextFieldColors()
                    )
                    DropdownMenu(expanded = projectMenuOpen, onDismissRequest = { projectMenuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("All projects") },
                            onClick = {
                                selectedProject = null
                                projectMenuOpen = false
                            }
                        )
                        projects.forEach { project ->
                            DropdownMenuItem(
                                text = { Text(project.name) },
                                onClick = {
                                    selectedProject = project.name
                                    projectMenuOpen = false
                                }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = startText,
                        onValueChange = {
                            startText = it
                            startError = it.isNotBlank() && parseDate(it) == null
                        },
                        label = { Text("Start date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = startError,
                        colors = financeOutlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = endText,
                        onValueChange = {
                            endText = it
                            endError = it.isNotBlank() && parseDate(it) == null
                        },
                        label = { Text("End date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = endError,
                        colors = financeOutlinedTextFieldColors()
                    )
                } else {
                    OutlinedTextField(
                        value = monthText,
                        onValueChange = { monthText = it },
                        label = { Text("Month (YYYY-MM)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = parseMonth(monthText) == null,
                        colors = financeOutlinedTextFieldColors()
                    )
                }

                OutlinedTextField(
                    value = filename,
                    onValueChange = { filename = it },
                    label = { Text("File name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = financeOutlinedTextFieldColors()
                )

                Button(
                    onClick = {
                        val outputName = if (filename.isBlank()) {
                            defaultFilename(ExportFormat.Csv.extension)
                        } else {
                            br.inf.cepp.financemanager.util.sanitizeFilename(ensureExtension(filename, ExportFormat.Csv.extension))
                        }
                        onExportRequested(
                            ExportRequest(
                                format = ExportFormat.Csv,
                                exportType = exportType,
                                filename = outputName,
                                selectedProject = selectedProject,
                                startDate = startDate,
                                endDate = endDate,
                                monthlyStartDate = monthlyStartDate,
                                monthlyEndDate = monthlyEndDate
                            )
                        )
                    },
                    enabled = (exportType == ExportType.Project && projectRangeValid) || (exportType == ExportType.Monthly && monthlyRangeValid),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export CSV")
                }

                Button(
                    onClick = {
                        val outputName = if (filename.isBlank()) {
                            defaultFilename(ExportFormat.Pdf.extension)
                        } else {
                            br.inf.cepp.financemanager.util.sanitizeFilename(ensureExtension(filename, ExportFormat.Pdf.extension))
                        }
                        onExportRequested(
                            ExportRequest(
                                format = ExportFormat.Pdf,
                                exportType = exportType,
                                filename = outputName,
                                selectedProject = selectedProject,
                                startDate = startDate,
                                endDate = endDate,
                                monthlyStartDate = monthlyStartDate,
                                monthlyEndDate = monthlyEndDate
                            )
                        )
                    },
                    enabled = (exportType == ExportType.Project && projectRangeValid) || (exportType == ExportType.Monthly && monthlyRangeValid),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export PDF")
                }

                status?.let { statusMessage ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = when {
                                statusMessage.contains("failed", ignoreCase = true) -> androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
                                statusMessage.contains("saved", ignoreCase = true) -> androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                                else -> androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                statusMessage,
                                color = when {
                                    statusMessage.contains("failed", ignoreCase = true) -> androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
                                    statusMessage.contains("saved", ignoreCase = true) -> androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                if (history.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Recent exports", fontWeight = FontWeight.SemiBold, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                history.forEach { item ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(
                                                displayExportName(item.path),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 13.sp,
                                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                                            )
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(
                                                    item.mimeType,
                                                    fontSize = 11.sp,
                                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                TextButton(onClick = { viewModel.shareFilePath(item.path, item.mimeType) }, modifier = Modifier.padding(0.dp)) {
                                                    Text("Share")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun displayExportName(path: String): String {
    return path.substringAfterLast('/').substringAfterLast('\\').ifBlank { path }
}

private fun ensureExtension(filename: String, extension: String): String {
    return if (filename.endsWith(".$extension", ignoreCase = true)) filename else "$filename.$extension"
}

private fun parseDate(value: String): LocalDate? = runCatching { LocalDate.parse(value) }.getOrNull()

private fun parseMonth(value: String): LocalDate? {
    val match = Regex("^\\d{4}-(\\d{1,2})$").matchEntire(value.trim()) ?: return null
    val monthNumber = match.groupValues[1].toInt()
    val month = runCatching { Month.values()[monthNumber - 1] }.getOrNull() ?: return null
    val year = value.trim().substringBefore('-').toIntOrNull() ?: return null
    return LocalDate(year, month, 1)
}

private fun isLeapYear(year: Int): Boolean {
    return year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)
}
