package br.inf.cepp.financemanager.ui.screen

import kotlinx.serialization.Serializable

// Navigation targets
import androidx.navigation.NavDestination

sealed interface Screen {
    @Serializable data object Dashboard : Screen
    @Serializable data object Import : Screen
    @Serializable data object Reconciliation : Screen
    @Serializable data object CategoryManager : Screen
    @Serializable data object Projects : Screen
    @Serializable data object AddExpense : Screen
    @Serializable data object AddIncome : Screen
    @Serializable data object Expenses : Screen
    @Serializable data object Accounts : Screen
    @Serializable data object Settings : Screen
    @Serializable data class Export(val initialType: String? = null) : Screen
}

// Maps a NavDestination (route string) to a Screen when possible.
// Keeps matching logic centralized so navigation selection is robust to route naming.
fun NavDestination?.toScreen(): Screen? {
    val route = this?.route ?: return null
    return when {
        route.endsWith("Dashboard") -> Screen.Dashboard
        route.endsWith("Import") -> Screen.Import
        route.endsWith("Reconciliation") -> Screen.Reconciliation
        route.endsWith("CategoryManager") -> Screen.CategoryManager
        route.endsWith("Projects") -> Screen.Projects
        route.endsWith("Project") -> Screen.Projects
        route.endsWith("AddExpense") -> Screen.AddExpense
        route.endsWith("AddIncome") -> Screen.AddIncome
        route.endsWith("Expenses") -> Screen.Expenses
        route.endsWith("Accounts") -> Screen.Accounts
        route.endsWith("Settings") -> Screen.Settings
        route.contains("Export") -> Screen.Export()
        else -> null
    }
}
