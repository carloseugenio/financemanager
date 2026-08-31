package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.Month
import br.inf.cepp.financemanager.util.today
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import br.inf.cepp.financemanager.ui.components.CategoriesViewModel
import br.inf.cepp.financemanager.ui.components.ExpenseCategoriesUiState
import br.inf.cepp.financemanager.ui.components.ExpenseSectionCard
import br.inf.cepp.financemanager.ui.components.IncomeSectionCard
import br.inf.cepp.financemanager.ui.screen.DashboardViewModel
import br.inf.cepp.financemanager.util.today
import com.composables.icons.lucide.*
import org.koin.compose.viewmodel.koinViewModel
import br.inf.cepp.financemanager.ui.util.lucidIconVector

@Composable
fun MainAppNavigation() {
    val navController = rememberNavController()

    val viewModel: CategoriesViewModel = koinViewModel<CategoriesViewModel>()
    val dashboardViewModel: DashboardViewModel = koinViewModel<DashboardViewModel>()
    val addExpenseViewModel: AddExpenseViewModel = koinViewModel<AddExpenseViewModel>()
    val addIncomeViewModel: AddIncomeViewModel = koinViewModel<AddIncomeViewModel>()
    val reconciliationViewModel: ReconciliationViewModel = koinViewModel<ReconciliationViewModel>()
    val projectsViewModel: ProjectsViewModel = koinViewModel<ProjectsViewModel>()
    val exportViewModel: ExportViewModel = koinViewModel<ExportViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val addExpenseCategories by addExpenseViewModel.categories.collectAsState()

    // Fetch data exactly once when this layout mounts
    LaunchedEffect(Unit) {
        viewModel.fetchData()
        dashboardViewModel.fetchData()
    }

    // Drawer state and scope
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Track current destination to show selected state in drawer
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.toScreen()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Drawer header
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "FinanceManager",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Navigation items with icon tint and selected visuals
                val itemModifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)

                NavigationDrawerItem(
                    modifier = itemModifier,
                    label = { Text("Dashboard", color = if (currentScreen == Screen.Dashboard) MaterialTheme.colorScheme.primary else LocalContentColor.current) },
                    selected = (currentScreen == Screen.Dashboard),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Dashboard)
                    },
                    icon = { Icon(Lucide.House, contentDescription = "Dashboard", tint = if (currentScreen == Screen.Dashboard) MaterialTheme.colorScheme.primary else LocalContentColor.current) }
                )

                NavigationDrawerItem(
                    modifier = itemModifier,
                    label = { Text("Import", color = if (currentScreen == Screen.Import) MaterialTheme.colorScheme.primary else LocalContentColor.current) },
                    selected = (currentScreen == Screen.Import),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Import)
                    },
                    icon = { Icon(Lucide.FileUp, contentDescription = "Import", tint = if (currentScreen == Screen.Import) MaterialTheme.colorScheme.primary else LocalContentColor.current) }
                )

                NavigationDrawerItem(
                    modifier = itemModifier,
                    label = { Text("Reconciliation", color = if (currentScreen == Screen.Reconciliation) MaterialTheme.colorScheme.primary else LocalContentColor.current) },
                    selected = (currentScreen == Screen.Reconciliation),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Reconciliation)
                    },
                    icon = { Icon(Lucide.CheckCheck, contentDescription = "Reconciliation", tint = if (currentScreen == Screen.Reconciliation) MaterialTheme.colorScheme.primary else LocalContentColor.current) }
                )

                NavigationDrawerItem(
                    modifier = itemModifier,
                    label = { Text("Expenses", color = if (currentScreen == Screen.Expenses) MaterialTheme.colorScheme.primary else LocalContentColor.current) },
                    selected = (currentScreen == Screen.Expenses),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Expenses)
                    },
                    icon = { Icon(Lucide.ShoppingCart, contentDescription = "Expenses", tint = if (currentScreen == Screen.Expenses) MaterialTheme.colorScheme.primary else LocalContentColor.current) }
                )

                NavigationDrawerItem(
                    modifier = itemModifier,
                    label = { Text("Categories", color = if (currentScreen == Screen.CategoryManager) MaterialTheme.colorScheme.primary else LocalContentColor.current) },
                    selected = (currentScreen == Screen.CategoryManager),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.CategoryManager)
                    },
                    icon = { Icon(Lucide.Settings, contentDescription = "Categories", tint = if (currentScreen == Screen.CategoryManager) MaterialTheme.colorScheme.primary else LocalContentColor.current) }
                )

                NavigationDrawerItem(
                    modifier = itemModifier,
                    label = { Text("Projects", color = if (currentScreen == Screen.Projects) MaterialTheme.colorScheme.primary else LocalContentColor.current) },
                    selected = (currentScreen == Screen.Projects),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Projects)
                    },
                    icon = { Icon(Lucide.FolderKanban, contentDescription = "Projects", tint = if (currentScreen == Screen.Projects) MaterialTheme.colorScheme.primary else LocalContentColor.current) }
                )

                NavigationDrawerItem(
                    modifier = itemModifier,
                    label = { Text("Add Expense", color = if (currentScreen == Screen.AddExpense) MaterialTheme.colorScheme.primary else LocalContentColor.current) },
                    selected = (currentScreen == Screen.AddExpense),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.AddExpense)
                    },
                    icon = { Icon(Lucide.Plus, contentDescription = "Add Expense", tint = if (currentScreen == Screen.AddExpense) MaterialTheme.colorScheme.primary else LocalContentColor.current) }
                )

                NavigationDrawerItem(
                    modifier = itemModifier,
                    label = { Text("Add Income", color = if (currentScreen == Screen.AddIncome) MaterialTheme.colorScheme.primary else LocalContentColor.current) },
                    selected = (currentScreen == Screen.AddIncome),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.AddIncome)
                    },
                    icon = { Icon(Lucide.TrendingUp, contentDescription = "Add Income", tint = if (currentScreen == Screen.AddIncome) MaterialTheme.colorScheme.primary else LocalContentColor.current) }
                )

                NavigationDrawerItem(
                    modifier = itemModifier,
                    label = { Text("Accounts", color = if (currentScreen == Screen.Accounts) MaterialTheme.colorScheme.primary else LocalContentColor.current) },
                    selected = (currentScreen == Screen.Accounts),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Accounts)
                    },
                    icon = { Icon(Lucide.DollarSign, contentDescription = "Accounts", tint = if (currentScreen == Screen.Accounts) MaterialTheme.colorScheme.primary else LocalContentColor.current) }
                )

                NavigationDrawerItem(
                    modifier = itemModifier,
                    label = { Text("Export", color = if (currentScreen == Screen.Export) MaterialTheme.colorScheme.primary else LocalContentColor.current) },
                    selected = (currentScreen == Screen.Export),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Export())
                    },
                    icon = { Icon(Lucide.Archive, contentDescription = "Export", tint = if (currentScreen == Screen.Export) MaterialTheme.colorScheme.primary else LocalContentColor.current) }
                )

                NavigationDrawerItem(
                    modifier = itemModifier,
                    label = { Text("Settings", color = if (currentScreen == Screen.Settings) MaterialTheme.colorScheme.primary else LocalContentColor.current) },
                    selected = (currentScreen == Screen.Settings),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Settings)
                    },
                    icon = { Icon(Lucide.Settings, contentDescription = "Settings", tint = if (currentScreen == Screen.Settings) MaterialTheme.colorScheme.primary else LocalContentColor.current) }
                )
            }
        }
    ) {
        // Top app bar + content scaffold
        androidx.compose.material3.Scaffold(
            topBar = {
                Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "FinanceManager", style = MaterialTheme.typography.titleMedium)

                        Row {
                            IconButton(onClick = { navController.navigate(Screen.Dashboard) }) {
                                Icon(Lucide.House, contentDescription = "Go to dashboard")
                            }
                            IconButton(onClick = { navController.navigate(Screen.AddExpense) }) {
                                Icon(Lucide.Plus, contentDescription = "Add expense")
                            }
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Lucide.Menu, contentDescription = "Open navigation")
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Dashboard
                ) {
                    composable<Screen.Dashboard> {
                        // manage a selectedMonth state at Nav host level so it survives recompositions
                        var selectedMonth by remember { mutableStateOf(today().month) }
                        LaunchedEffect(selectedMonth) {
                            dashboardViewModel.fetchData(selectedMonth)
                        }

                        FinanceDashboardScreen(
                            viewModel = dashboardViewModel,
                            selectedMonth = selectedMonth,
                            onMonthChange = { selectedMonth = it },
                            onManageCategoriesClick = {
                                navController.navigate(Screen.CategoryManager)
                            },
                            onAddExpenseClick = {
                                navController.navigate(Screen.AddExpense)
                            },
                            onAddIncomeClick = {
                                navController.navigate(Screen.AddIncome)
                            },
                            onExportClick = {
                                navController.navigate(Screen.Export())
                            }
                        )
                    }

                    composable<Screen.Import> {
                        ImportScreen(
                            onBack = { navController.popBackStack() },
                            onReviewDrafts = { navController.navigate(Screen.Reconciliation) }
                        )
                    }

                    composable<Screen.Reconciliation> {
                        ReconciliationScreen(
                            viewModel = reconciliationViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<Screen.Expenses> {
                        ExpensesScreen(onOpenExport = { navController.navigate(Screen.Export(initialType = "Monthly")) })
                    }

                    composable<Screen.Accounts> {
                        AccountsScreen(onBack = { navController.popBackStack() })
                    }

                    composable<Screen.Settings> {
                        SettingsScreen(onBack = { navController.popBackStack() })
                    }

                    composable<Screen.CategoryManager> {
                        CategoryManagerScreen(
                            categories = when (val state = uiState) {
                                is ExpenseCategoriesUiState.Success -> state.data
                                else -> emptyList()
                            },
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onSaveNewCategory = { name, hexColor, iconKey ->
                                viewModel.createNewCategory(name, hexColor, iconKey)
                            },
                            onUpdateCategory = { category ->
                                viewModel.updateCategory(category)
                            },
                            onDeleteCategory = { categoryName ->
                                viewModel.deleteCategory(categoryName)
                            }
                        )
                    }

                    composable<Screen.Projects> {
                        ProjectsScreen(
                            viewModel = projectsViewModel,
                            onBack = { navController.popBackStack() },
                            onExportClick = { navController.navigate(Screen.Export(initialType = "Project")) }
                        )
                    }

                    composable<Screen.Export> { backStackEntry ->
                        val exportRoute = backStackEntry.toRoute<Screen.Export>()
                        ExportScreen(
                            viewModel = exportViewModel,
                            onBack = { navController.popBackStack() },
                            initialType = exportRoute.initialType
                        )
                    }

                    composable<Screen.AddExpense> {
                        AddExpenseScreen(
                            categories = addExpenseCategories,
                            onBackClick = { navController.popBackStack() },
                            onSaveExpense = { desc, amt, cat, date, status, src, recurrence, reminder ->
                                addExpenseViewModel.saveExpense(desc, amt, cat, date, status, src, recurrence, reminder)
                            }
                        )
                    }

                    composable<Screen.AddIncome> {
                        AddIncomeScreen(
                            onBackClick = { navController.popBackStack() },
                            onSaveIncome = { desc, amt, category, date, source, status, recurrence, existingIncome ->
                                addIncomeViewModel.saveIncome(desc, amt, category, date, source, status, recurrence, existingIncome)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Main screen Layout
 * Serves as the main scroll container utilizing a lazy structure.
 * This handles performance optimizations when dealing with large transaction sets.
 */
@Composable
fun FinanceDashboardScreen(
    viewModel: DashboardViewModel,
    selectedMonth: Month = today().month,
    onMonthChange: (Month) -> Unit = {},
    onManageCategoriesClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onAddIncomeClick: () -> Unit,
    onExportClick: () -> Unit
) {
    val viewData by viewModel.viewData.collectAsState()
    val plannedExpenses by viewModel.plannedExpenses.collectAsState()
    val plannedIncomes by viewModel.plannedIncomes.collectAsState()
    val recentExpenses by viewModel.recentExpenses.collectAsState()
    
    val totalSpentAmount by viewModel.totalSpentAmount.collectAsState()
    val transactionCount by viewModel.transactionCount.collectAsState()
    val accountCount by viewModel.accountCount.collectAsState()
    val previousMonthSpentAmount by viewModel.previousMonthSpentAmount.collectAsState()
    val plannedBudgetAmount by viewModel.plannedBudgetAmount.collectAsState()
    val budgetVsActual by viewModel.budgetVsActual.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val utils = br.inf.cepp.financemanager.util.LocalPlatformUtils.current
    val totalSpent = "${utils.getCurrentCurrencySymbol()} ${"%.2f".format(totalSpentAmount)}"
    val plannedBudget = "${utils.getCurrentCurrencySymbol()} ${"%.2f".format(plannedBudgetAmount)}"
    val budgetUtilizationPercent = budgetVsActual?.utilizationPercent ?: if (plannedBudgetAmount > 0.0) (totalSpentAmount / plannedBudgetAmount) * 100.0 else 0.0
    val monthLabel = selectedMonth.name.lowercase().replaceFirstChar { it.uppercase() }
    val previousMonthDisplay = "${utils.getCurrentCurrencySymbol()} ${"%.2f".format(previousMonthSpentAmount)}"
    val previousDeltaPercent = if (previousMonthSpentAmount > 0.0) {
        ((totalSpentAmount - previousMonthSpentAmount) / previousMonthSpentAmount) * 100.0
    } else {
        0.0
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Month navigation row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onMonthChange(previousMonth(selectedMonth)) }) {
                            Icon(Lucide.ChevronLeft, contentDescription = "Previous month")
                        }

                        Text(
                            text = monthLabel,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )

                        IconButton(onClick = { onMonthChange(nextMonth(selectedMonth)) }) {
                            Icon(Lucide.ChevronRight, contentDescription = "Next month")
                        }
                    }
                }

                item {
                    when {
                        isLoading -> LoadingOverviewCard()
                        errorMessage != null -> ErrorOverviewCard(errorMessage!!)
                        else -> TopSummaryCard(
                            totalSpent = totalSpent,
                            transactionCount = transactionCount,
                            categoryCount = viewData.size,
                            accountCount = accountCount,
                            monthLabel = monthLabel,
                            plannedBudget = plannedBudget,
                            budgetUtilizationPercent = budgetUtilizationPercent
                        )
                    }
                }

                if (!isLoading && errorMessage == null) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onExportClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("Export data")
                            }
                            Button(
                                onClick = onAddIncomeClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text("Add income")
                            }
                            Button(
                                onClick = onManageCategoriesClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text("Manage categories")
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Budgeted vs actual",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                if (viewData.isEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = emptyExpensesMessage(selectedMonth),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    DonutChart(categories = viewData, selectedMonth = selectedMonth, totalAmountLabel = totalSpent)
                                    Spacer(modifier = Modifier.height(20.dp))
                                    BudgetVsActualCard(
                                        actualAmount = totalSpent,
                                        budgetAmount = plannedBudget,
                                        utilizationPercent = budgetUtilizationPercent
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    viewData.forEach { category ->
                                        CategoryProgressRow(category = category)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        MonthlyPerformanceCard(
                            currentAmount = totalSpent,
                            previousAmount = previousMonthDisplay,
                            deltaPercent = previousDeltaPercent
                        )
                    }

                    item {
                        ExpenseSectionCard(
                            title = "Upcoming planned expenses",
                            sectionIcon = Lucide.Calendar,
                            iconTint = MaterialTheme.colorScheme.secondary,
                            items = plannedExpenses
                        )
                    }

                    item {
                        IncomeSectionCard(
                            title = "Upcoming planned incomes",
                            sectionIcon = Lucide.TrendingUp,
                            iconTint = Color(0xFF287A5A),
                            items = plannedIncomes
                        )
                    }

                    item {
                        ExpenseSectionCard(
                            title = "Recent expenses",
                            sectionIcon = Lucide.TrendingDown,
                            iconTint = MaterialTheme.colorScheme.error,
                            items = recentExpenses
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingOverviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Loading dashboard", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text("Fetching balances, expenses, and planned items.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ErrorOverviewCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Dashboard unavailable", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onErrorContainer)
            Text(message, color = MaterialTheme.colorScheme.onErrorContainer)
        }
    }
}

fun nextMonth(month: Month): Month = when (month) {
    Month.DECEMBER -> Month.JANUARY
    else -> Month.values()[month.ordinal + 1]
}

fun previousMonth(month: Month): Month = when (month) {
    Month.JANUARY -> Month.DECEMBER
    else -> Month.values()[month.ordinal - 1]
}

fun emptyExpensesMessage(month: Month): String = "No expenses for ${month.name.lowercase().replaceFirstChar { it.uppercase() }}"
