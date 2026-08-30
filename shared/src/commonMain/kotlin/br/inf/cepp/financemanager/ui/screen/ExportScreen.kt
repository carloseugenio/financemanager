package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.TextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.IconButton
import br.inf.cepp.financemanager.ui.util.HexColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ExportScreen(viewModel: ExportViewModel, onBack: () -> Unit) {
    val status by viewModel.lastExportStatus.collectAsState()
    val projects by viewModel.projects.collectAsState()
    var exportType by remember { mutableStateOf("Project") }
    var selectedProject by remember { mutableStateOf<String?>(null) }
    var startText by remember { mutableStateOf("") }
    var endText by remember { mutableStateOf("") }
    var filename by remember { mutableStateOf("") }

    val locale = androidx.compose.ui.platform.LocalLocale.current
    val history by viewModel.history.collectAsState()

    fun defaultTimestamp(): String {
            val d = br.inf.cepp.financemanager.util.today()
            return "%04d%02d%02d".format(d.year, d.monthNumber, d.dayOfMonth)
    }

    fun updateDefaultFilename() {
        val base = when (exportType) {
            "Project" -> selectedProject ?: "projects"
            else -> "expenses"
        }
        filename = "${base}_export_${defaultTimestamp()}.csv"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color(0xFFF9FAFC)
    ) { paddingValues ->
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues), color = Color(0xFFF9FAFC)) {

            Column(modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Export data", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Choose what to export and the desired format. CSV generation is available for projects and date ranges.")

                // Export type selector
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val types = listOf("Project", "Expenses")
                    var expandedType by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = exportType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Export item") },
                        trailingIcon = { androidx.compose.material3.IconButton(onClick = { expandedType = true }) { androidx.compose.material3.Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null) } },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                        types.forEach { t ->
                            DropdownMenuItem(text = { Text(t) }, onClick = { exportType = t; expandedType = false; updateDefaultFilename() })
                        }
                    }
                }

                if (exportType == "Project") {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Choose project to export", fontWeight = FontWeight.SemiBold)
                            var expanded by remember { mutableStateOf(false) }
                            val projectNames = listOf("All Projects") + projects.map { it.name }
                            OutlinedTextField(
                                value = selectedProject ?: "All Projects",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Project") },
                                trailingIcon = { IconButton(onClick = { expanded = true }) { androidx.compose.material3.Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null) } },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                projectNames.forEach { pn ->
                                    DropdownMenuItem(text = { Text(pn) }, onClick = { selectedProject = if (pn == "All Projects") null else pn; expanded = false; updateDefaultFilename() })
                                }
                            }

                            OutlinedTextField(value = filename, onValueChange = { filename = it }, label = { Text("File name") }, modifier = Modifier.fillMaxWidth())

                            Button(onClick = { viewModel.exportProjectsCsv(if (filename.isBlank()) { updateDefaultFilename(); filename } else filename, selectedProject) }, modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))) {
                                Text("Export to CSV", color = Color.White)
                            }
                            Button(onClick = { viewModel.exportProjectsPdf(if (filename.isBlank()) { updateDefaultFilename(); filename = filename.replaceAfterLast('.', "pdf"); filename } else filename.replaceAfterLast('.', "pdf"), selectedProject) }, modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))) {
                                Text("Export to PDF", color = Color.White)
                            }
                        }
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Date range exports", fontWeight = FontWeight.SemiBold)
                            Text("Enter start and end dates as YYYY-MM-DD")
                            TextField(value = startText, onValueChange = { startText = it }, label = { Text("Start (YYYY-MM-DD)") })
                            TextField(value = endText, onValueChange = { endText = it }, label = { Text("End (YYYY-MM-DD)") })

                            OutlinedTextField(value = filename, onValueChange = { filename = it }, label = { Text("File name") }, modifier = Modifier.fillMaxWidth())

                            Button(onClick = {
                                try {
                                    val start = LocalDate.parse(startText)
                                    val end = LocalDate.parse(endText)
                                    val outName = if (filename.isBlank()) { updateDefaultFilename(); filename } else filename
                                    viewModel.exportMonthlyCsv(outName, start, end)
                                } catch (t: Throwable) {
                                    viewModel.exportMonthlyCsv(if (filename.isBlank()) { updateDefaultFilename(); filename } else filename)
                                }
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))) {
                                Text("Export to CSV", color = Color.White)
                            }

                            Button(onClick = {
                                try {
                                    val start = LocalDate.parse(startText)
                                    val end = LocalDate.parse(endText)
                                    val outName = if (filename.isBlank()) { updateDefaultFilename(); filename = filename.replaceAfterLast('.', "pdf"); filename } else filename.replaceAfterLast('.', "pdf")
                                    viewModel.exportMonthlyPdf(outName, start, end)
                                } catch (t: Throwable) {
                                    viewModel.exportMonthlyPdf(if (filename.isBlank()) { updateDefaultFilename(); filename = filename.replaceAfterLast('.', "pdf"); filename } else filename.replaceAfterLast('.', "pdf"))
                                }
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))) {
                                Text("Export to PDF", color = Color.White)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    TextButton(onClick = { onBack() }) { Text("Close") }
                                    TextButton(onClick = { onBack() }) { Text("Back") }
                                }
                            }
                        }
                    }
                }

                status?.let {
                    Text("Status: $it", color = Color(0xFF374151))
                }

                if (history.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Recent exports", fontWeight = FontWeight.SemiBold)
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth().heightIn(max = 260.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(history) { path ->
                                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(path)
                                        val mime = if (path.endsWith(".pdf")) "application/pdf" else "text/csv"
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                            Button(onClick = { viewModel.shareFilePath(path, mime) }) { Text("Share") }
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
