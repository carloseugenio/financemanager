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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.model.ProjectItem
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
import br.inf.cepp.financemanager.ui.components.financeOutlinedTextFieldColors
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(viewModel: ProjectsViewModel, onBack: () -> Unit, onExportClick: () -> Unit) {
    val categoriesViewModel: CategoriesViewModel = koinViewModel()
    val plans by viewModel.plans.collectAsState()
    val projectItems by viewModel.projectItems.collectAsState()
    val categoriesState by categoriesViewModel.uiState.collectAsState()
    var showCreateSheet by remember { mutableStateOf(false) }
    var showItemSheetFor by remember { mutableStateOf<String?>(null) }
    var editingPlan by remember { mutableStateOf<ProjectPlan?>(null) }
    var editingItem by remember { mutableStateOf<ProjectItem?>(null) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        categoriesViewModel.fetchData()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                    TextButton(onClick = onExportClick) {
                        Text("Export", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { showCreateSheet = true }) {
                        Text("New", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        color = MaterialTheme.colorScheme.background
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
                    Text("No projects yet", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(top = 16.dp))
                    Text(
                        text = "Create a project plan to track budgeted vs actual spending.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        ProjectCard(
                            plan = plan,
                            plannedItems = projectItems[plan.name].orEmpty(),
                            onAddItem = { showItemSheetFor = plan.name; editingItem = null },
                            onEditPlan = { editingPlan = plan },
                            onEditItem = { editingItem = it },
                            onDeleteItem = { viewModel.deleteItem(it) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateSheet) {
        PlanCreationSheet(
            sheetState = sheetState,
            onDismiss = { showCreateSheet = false },
            onSave = { plan ->
                viewModel.savePlan(plan)
                showCreateSheet = false
            }
        )
    }

    if (editingPlan != null) {
        PlanCreationSheet(
            sheetState = sheetState,
            existingPlan = editingPlan,
            onDismiss = { editingPlan = null },
            onSave = { plan ->
                viewModel.savePlan(plan)
                editingPlan = null
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

    if (editingItem != null) {
        val categories = (categoriesState as? br.inf.cepp.financemanager.ui.components.ExpenseCategoriesUiState.Success)?.data.orEmpty()
        PlanItemCreationSheet(
            sheetState = sheetState,
            planName = editingItem!!.planName,
            categories = categories,
            existingItem = editingItem,
            onDismiss = { editingItem = null },
            onSave = { planName, description, amount, expectedDate, category ->
                val updated = editingItem!!.copy(
                    planName = planName,
                    category = category,
                    description = description,
                    budget = amount,
                    expectedDate = expectedDate,
                    relatedExpense = editingItem!!.relatedExpense.copy(title = "", date = expectedDate, amount = 0.0)
                )
                viewModel.saveItem(updated)
                editingItem = null
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
    existingItem: ProjectItem? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, LocalDate, ExpenseCategory) -> Unit
) {
    val initialDate = existingItem?.expectedDate ?: today()
    var description by remember(existingItem?.id) { mutableStateOf(existingItem?.description ?: "") }
    var amountText by remember(existingItem?.id) { mutableStateOf(existingItem?.budget?.toString() ?: "") }
    var selectedCategory by remember(existingItem?.id) { mutableStateOf(existingItem?.category ?: categories.firstOrNull()) }
    var expectedDate by remember(existingItem?.id) { mutableStateOf(initialDate) }
    var dayText by remember(existingItem?.id) { mutableStateOf(initialDate.day.toString()) }
    var month by remember(existingItem?.id) { mutableStateOf(initialDate.month) }
    var yearText by remember(existingItem?.id) { mutableStateOf(initialDate.year.toString()) }
    val dayFocus = remember { FocusRequester() }
    val yearFocus = remember { FocusRequester() }

    fun updateExpectedDateFromParts() {
        val parsedYear = yearText.toIntOrNull() ?: initialDate.year
        val parsedDay = dayText.toIntOrNull()?.coerceIn(1, 31) ?: 1
        expectedDate = LocalDate(parsedYear, month, parsedDay.coerceIn(1, 28))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (existingItem == null) "Add planned item to $planName" else "Edit planned item",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                colors = financeOutlinedTextFieldColors()
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
                modifier = Modifier.fillMaxWidth(),
                colors = financeOutlinedTextFieldColors()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = dayText,
                    onValueChange = { v ->
                        if (v.isEmpty() || v.toIntOrNull() != null) {
                            dayText = v
                            if (v.isNotEmpty()) {
                                val parsed = v.toIntOrNull() ?: return@OutlinedTextField
                                if (parsed in 1..31) {
                                    expectedDate = LocalDate(yearText.toIntOrNull() ?: initialDate.year, month, parsed.coerceIn(1, 28))
                                }
                            }
                        }
                    },
                    label = { Text("Day") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
                    modifier = Modifier.fillMaxWidth(0.24f).focusRequester(dayFocus),
                    shape = RoundedCornerShape(12.dp),
                    colors = financeOutlinedTextFieldColors()
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
                            updateExpectedDateFromParts()
                        }) { Text("Next") }
                    },
                    modifier = Modifier.fillMaxWidth(0.42f),
                    shape = RoundedCornerShape(12.dp),
                    colors = financeOutlinedTextFieldColors()
                )
                OutlinedTextField(
                    value = yearText,
                    onValueChange = { v ->
                        if (v.isEmpty() || v.toIntOrNull() != null) {
                            yearText = v
                            if (v.isNotEmpty()) {
                                val parsedYear = v.toIntOrNull() ?: return@OutlinedTextField
                                expectedDate = LocalDate(parsedYear, month, dayText.toIntOrNull()?.coerceIn(1, 28) ?: 1)
                            }
                        }
                    },
                    label = { Text("Year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth().focusRequester(yearFocus),
                    shape = RoundedCornerShape(12.dp),
                    colors = financeOutlinedTextFieldColors()
                )
            }

            val utils = br.inf.cepp.financemanager.util.LocalPlatformUtils.current
            OutlinedTextField(
                value = amountText,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amountText = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text(utils.getCurrentCurrencySymbol() + " ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                singleLine = true,
                colors = financeOutlinedTextFieldColors()
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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(if (existingItem == null) "Add item" else "Save item", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
private fun ProjectCard(
    plan: ProjectPlan,
    plannedItems: List<br.inf.cepp.financemanager.model.ProjectItem>,
    onAddItem: () -> Unit = {},
    onEditPlan: () -> Unit = {},
    onEditItem: (br.inf.cepp.financemanager.model.ProjectItem) -> Unit = {},
    onDeleteItem: (br.inf.cepp.financemanager.model.ProjectItem) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(plan.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(plan.status.name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = onEditPlan) {
                    Text("Edit", color = MaterialTheme.colorScheme.secondary)
                }
            }

            Text(
                text = "${plan.startDate} — ${plan.endDate}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Budget", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("R$ %.2f".format(plan.budget), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Progress", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("0%", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary)
            }

            if (plannedItems.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Planned expenses", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                    plannedItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                Text("R$ %.2f".format(item.budget), color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                TextButton(onClick = { onEditItem(item) }) { Text("Edit") }
                                TextButton(onClick = { onDeleteItem(item) }) { Text("Delete") }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onAddItem,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text("Add planned item")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanCreationSheet(
    sheetState: androidx.compose.material3.SheetState,
    existingPlan: ProjectPlan? = null,
    onDismiss: () -> Unit,
    onSave: (ProjectPlan) -> Unit
) {
    var name by remember(existingPlan?.name) { mutableStateOf(existingPlan?.name ?: "") }
    var startDate by remember(existingPlan?.name) { mutableStateOf(existingPlan?.startDate?.toString() ?: today().toString()) }
    var endDate by remember(existingPlan?.name) { mutableStateOf(existingPlan?.endDate?.toString() ?: "2026-09-30") }
    var budget by remember(existingPlan?.name) { mutableStateOf(existingPlan?.budget?.toString() ?: "2500.00") }
    val locale = androidx.compose.ui.platform.LocalLocale.current
    val datePlaceholder = if (locale.language == "pt") "dd/MM/yyyy" else "yyyy-MM-dd"
    val focusManager = LocalFocusManager.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(if (existingPlan == null) "New project plan" else "Edit project plan", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Project name") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = existingPlan != null,
                colors = financeOutlinedTextFieldColors()
            )

            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Start date ($datePlaceholder)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(datePlaceholder) },
                colors = financeOutlinedTextFieldColors()
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("End date ($datePlaceholder)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(datePlaceholder) },
                colors = financeOutlinedTextFieldColors()
            )

            OutlinedTextField(
                value = budget,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) budget = it },
                label = { Text("Budget") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text(br.inf.cepp.financemanager.util.LocalPlatformUtils.current.getCurrentCurrencySymbol() + " ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                singleLine = true,
                colors = financeOutlinedTextFieldColors()
            )

            Button(
                onClick = {
                    val parsedBudget = budget.toDoubleOrNull() ?: 0.0
                    val parsedStart = runCatching { LocalDate.parse(startDate) }.getOrDefault(LocalDate(2026, 8, 1))
                    val parsedEnd = runCatching { LocalDate.parse(endDate) }.getOrDefault(LocalDate(2026, 9, 30))
                    if (name.isNotBlank()) {
                        onSave(
                            (existingPlan ?: ProjectPlan(name.trim(), parsedStart, parsedEnd, parsedBudget, ProjectStatus.ACTIVE)).copy(
                                startDate = parsedStart,
                                endDate = parsedEnd,
                                budget = parsedBudget
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(if (existingPlan == null) "Save plan" else "Update plan", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}
