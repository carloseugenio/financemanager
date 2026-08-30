package br.inf.cepp.financemanager.repository

import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.ui.util.HexColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

val financeInstitutions = listOf(
    FinanceInstitution("North Bank", "Bank"),
    FinanceInstitution("Blue Credit", "Credit Union")
)

val allCategories = mutableListOf(
    ExpenseCategory("Groceries", HexColor("#F59E0B"), "shopping-cart"),
    ExpenseCategory("Transport", HexColor("#3B82F6"), "car"),
    ExpenseCategory("Health", HexColor("#EF4444"), "heart"),
    ExpenseCategory("Subscriptions", HexColor("#8B5CF6"), "gamepad-2")
)

val allAccounts = listOf(
    Account("Checking", AccountType.CHECKING, financeInstitutions[0], "0001", "12345-6", "PIX", 2500.0, 5, 1450.0, HexColor("#22C55E"), "dollar-sign"),
    Account("Credit Card", AccountType.CREDIT, financeInstitutions[1], "0002", "98765-4", "", 6000.0, 12, -320.5, HexColor("#EF4444"), "credit-card")
)

val allExpenses = listOf(
    Expense(description = "Market run", category = allCategories[0], date = LocalDate(2026, 8, 2), amount = 214.35, status = ExpenseStatus.CONFIRMED, source = ExpenseSource.MANUAL),
    Expense(description = "Bus pass", category = allCategories[1], date = LocalDate(2026, 8, 3), amount = 98.0, status = ExpenseStatus.CONFIRMED, source = ExpenseSource.MANUAL),
    Expense(description = "Pharmacy", category = allCategories[2], date = LocalDate(2026, 8, 6), amount = 76.9, status = ExpenseStatus.CONFIRMED, source = ExpenseSource.MANUAL),
    Expense(description = "Streaming renewal", category = allCategories[3], date = LocalDate(2026, 8, 8), amount = 39.9, status = ExpenseStatus.CONFIRMED, source = ExpenseSource.MANUAL),
    Expense(description = "Draft grocery import", category = allCategories[0], date = LocalDate(2026, 8, 14), amount = 132.12, status = ExpenseStatus.DRAFT, source = ExpenseSource.IMPORT),
    Expense(
        description = "Monthly rent",
        category = allCategories[1],
        date = LocalDate(2026, 9, 1),
        amount = 1800.0,
        status = ExpenseStatus.PLANNED,
        source = ExpenseSource.MANUAL,
        recurrence = RecurrenceRule(RecurrenceFrequency.MONTHLY, interval = 1),
        reminder = Reminder(enabled = true, method = ReminderMethod.NOTIFICATION, leadTimeMinutes = 1440)
    )
)

val plannedExpenses = listOf(
    ExpenseItem(title = "Vacation flight", date = LocalDate(2026, 9, 10), amount = 2500.0, iconKey = "car", iconColor = HexColor("#3B82F6")),
    ExpenseItem(title = "Hotel stay", date = LocalDate(2026, 9, 11), amount = 3200.0, iconKey = "home", iconColor = HexColor("#14B8A6"))
)

val recentExpenses = listOf(
    ExpenseItem(title = "Coffee shop", date = LocalDate(2026, 8, 28), amount = 18.5, iconKey = "coffee", iconColor = HexColor("#A855F7")),
    ExpenseItem(title = "Ride share", date = LocalDate(2026, 8, 27), amount = 24.9, iconKey = "car", iconColor = HexColor("#3B82F6"))
)

fun List<Expense>.amountTotal(): Double = sumOf { it.amount }

fun List<Expense>.confirmed(): List<Expense> = filter { it.status == ExpenseStatus.CONFIRMED }

fun List<Expense>.onMonth(month: Month): List<Expense> = filter { it.date.month == month }
