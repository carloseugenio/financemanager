package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.model.ProjectPlan
import br.inf.cepp.financemanager.model.ProjectStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import br.inf.cepp.financemanager.util.today
import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.model.ExpenseItem
import br.inf.cepp.financemanager.ui.util.HexColor
import com.composables.icons.lucide.FolderKanban
import com.composables.icons.lucide.Lucide
import br.inf.cepp.financemanager.ui.components.CategoriesViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(viewModel: ProjectsViewModel, onBack: () -> Unit) {
    val categoriesViewModel: CategoriesViewModel = koinViewModel()
    val plans by viewModel.plans.collectAsState()
    val projectItems by viewModel.projectItems.collectAsState()
    val categoriesState by categoriesViewModel.uiState.collectAsState()
    var showCreateSheet by remember { mutableStateOf(false) }
    var showItemSheetFor by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        categoriesViewModel.fetchData()
    }

    Scaffold(
        containerColor = Color(0xFFF9FAFC),
        topBar = {
            TopAppBar(
                title = { Text("Projects", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { showCreateSheet = true }) {
                        Text("New", color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Color(0xFFF9FAFC)
        ) {
            if (plans.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEF2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Lucide.FolderKanban, contentDescription = "Projects", tint = Color(0xFF4F46E5), modifier = Modifier.size(32.dp))
                    }
                    Text(
                        text = "No projects yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF111827),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = "Create a project plan to track budgeted vs actual spending.",
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(plans) { plan ->
                        ProjectCard(plan = plan, plannedItems = projectItems[plan.name].orEmpty(), onAddItem = { showItemSheetFor = plan.name })
                    }
                }
            }
        }
    }

    if (showCreateSheet) {
        PlanCreationSheet(
            sheetState = sheetState,
            onDismiss = { showCreateSheet = false },
            onSave = { name, startDate, endDate, budget ->
                viewModel.savePlan(ProjectPlan(name = name, startDate = startDate, endDate = endDate, budget = budget, status = ProjectStatus.ACTIVE))
                showCreateSheet = false
            }
        )
    }

    if (showItemSheetFor != null) {
        val categories = (categoriesState as? br.inf.cepp.financemanager.ui.components.ExpenseCategoriesUiState.Success)?.data.orEmpty()
        PlanItemCreationSheet(
            sheetState = sheetState,
            planName = showItemSheetFor!!,
            categories = categories,
            onDismiss = { showItemSheetFor = null },
            onSave = { planName, description, amount, expectedDate, category ->
                val item = br.inf.cepp.financemanager.model.ProjectItem(
                    id = 0L,
                    planName = planName,
                    category = category,
                    description = description,
                    budget = amount,
                    actual = 0.0,
                    expectedDate = expectedDate,
                    relatedExpense = br.inf.cepp.financemanager.model.ExpenseItem(id = 0, title = "", date = expectedDate, amount = 0.0, iconKey = "", iconColor = HexColor("#000000"))
                )
                viewModel.saveItem(item)
                showItemSheetFor = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanItemCreationSheet(
    sheetState: androidx.compose.material3.SheetState,
    planName: String,
    categories: List<ExpenseCategory>,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, LocalDate, ExpenseCategory) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var expectedDate by remember { mutableStateOf(today()) }
    var day by remember { mutableStateOf(expectedDate.day) }
    var month by remember { mutableStateOf(expectedDate.month) }
    var year by remember { mutableStateOf(expectedDate.year.toString()) }
    val dayFocus = remember { FocusRequester() }
    val yearFocus = remember { FocusRequester() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Add planned item to $planName", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF111827))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = selectedCategory?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    Row {
                        TextButton(onClick = {
                            if (categories.isNotEmpty()) {
                                val currentIndex = categories.indexOf(selectedCategory)
                                val nextIndex = if (currentIndex < 0) 0 else (currentIndex + 1) % categories.size
                                selectedCategory = categories[nextIndex]
                            }
                        }) { Text("Next") }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = day.toString(),
                    onValueChange = { v ->
                        v.toIntOrNull()?.takeIf { it in 1..31 }?.let {
                            day = it
                            expectedDate = LocalDate(year.toIntOrNull() ?: today().year, month, day.coerceIn(1, 28))
                        }
                    },
                    label = { Text("Day") },
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number, imeAction = androidx.compose.ui.text.input.ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
                    modifier = Modifier.fillMaxWidth(0.24f).focusRequester(dayFocus),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = month.name.replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Month") },
                    trailingIcon = {
                        TextButton(onClick = {
                            val values = Month.entries.toTypedArray()
                            val idx = values.indexOf(month)
                            month = values[(idx + 1) % values.size]
                            expectedDate = LocalDate(year.toIntOrNull() ?: today().year, month, day.coerceIn(1, 28))
                        }) { Text("Next") }
                    },
                    modifier = Modifier.fillMaxWidth(0.42f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { v ->
                        if (v.isEmpty() || v.toIntOrNull() != null) {
                            year = v
                            if (v.toIntOrNull() != null && v.length == 4) {
                                expectedDate = LocalDate(v.toInt(), month, day.coerceIn(1, 28))
                            }
                        }
                    },
                    label = { Text("Year") },
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number, imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth().focusRequester(yearFocus),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            val utils = br.inf.cepp.financemanager.util.LocalPlatformUtils.current
            OutlinedTextField(
                value = amountText,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amountText = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text(utils.getCurrentCurrencySymbol() + " ") }
            )

            Button(
                onClick = {
                    val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
                    if (description.isNotBlank() && selectedCategory != null) {
                        onSave(planName, description.trim(), parsedAmount, expectedDate, selectedCategory!!)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
            ) {
                Text("Add item")
            }
        }
    }
}

@Composable
private fun ProjectCard(plan: ProjectPlan, plannedItems: List<br.inf.cepp.financemanager.model.ProjectItem>, onAddItem: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(plan.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF111827))
                Text(plan.status.name, fontSize = 12.sp, color = Color(0xFF0F766E))
            }

            Text(
                text = "${plan.startDate} — ${plan.endDate}",
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Budget", color = Color(0xFF64748B))
                Text("R$ %.2f".format(plan.budget), fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Progress", color = Color(0xFF64748B))
                Text("0%", fontWeight = FontWeight.SemiBold, color = Color(0xFF10B981))
            }

            if (plannedItems.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Planned expenses", fontWeight = FontWeight.SemiBold, color = Color(0xFF1F2937), fontSize = 14.sp)
                    plannedItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.description, color = Color(0xFF475569), fontSize = 13.sp)
                            Text("R$ %.2f".format(item.budget), color = Color(0xFF4F46E5), fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                }
            }

            Button(onClick = onAddItem, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text("Add planned item")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanCreationSheet(
    sheetState: androidx.compose.material3.SheetState,
    onDismiss: () -> Unit,
    onSave: (String, LocalDate, LocalDate, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(today().toString()) }
    var endDate by remember { mutableStateOf("2026-09-30") }
    var budget by remember { mutableStateOf("2500.00") }
    val locale = androidx.compose.ui.platform.LocalLocale.current
    val datePlaceholder = if (locale.language == "pt") "dd/MM/yyyy" else "yyyy-MM-dd"
    val utils = br.inf.cepp.financemanager.util.LocalPlatformUtils.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("New project plan", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF111827))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Project name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Start date ($datePlaceholder)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(datePlaceholder) }
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("End date ($datePlaceholder)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(datePlaceholder) }
            )

            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Budget") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text(utils.getCurrentCurrencySymbol() + " ") }
            )

            Button(
                onClick = {
                    val parsedBudget = budget.toDoubleOrNull() ?: 0.0
                    val parsedStart = runCatching { LocalDate.parse(startDate) }.getOrDefault(LocalDate(2026, 8, 1))
                    val parsedEnd = runCatching { LocalDate.parse(endDate) }.getOrDefault(LocalDate(2026, 9, 30))
                    if (name.isNotBlank()) {
                        onSave(name.trim(), parsedStart, parsedEnd, parsedBudget)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
            ) {
                Text("Save plan")
            }
        }
    }
}
