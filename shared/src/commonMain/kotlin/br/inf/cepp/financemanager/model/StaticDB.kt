package br.inf.cepp.financemanager.model

import br.inf.cepp.financemanager.ui.util.LucideIcons
import br.inf.cepp.financemanager.ui.util.lucidIconVector
import br.inf.cepp.financemanager.ui.util.HexColor
import br.inf.cepp.financemanager.ui.util.toComposeColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

val financeInstitutions = listOf(
    FinanceInstitution(name =  "Brazil Bank", type = "Bank"),
    FinanceInstitution(name = "Bradesco", type =  "Bank")
)
val allAccounts = listOf(
    Account(name = "Brazil Bank Account", AccountType.CHECKING, financeInstitutions[0], "1111", "11111111", "", 1000.0, 5, 100.0, HexColor("#FFFF00"),
        LucideIcons.DollarSign.name),
    Account(name = "Bradesco Account", AccountType.CHECKING, financeInstitutions[1], "2222", "2222222", "", 2000.0, 10, 300.0,
        HexColor("#FF0000"), LucideIcons.DollarSign.name)
)

val allCategories = listOf(
    ExpenseCategory(name ="Grocery", color = HexColor("0xFFFACC15"), iconKey = LucideIcons.ShoppingCart.name),
    ExpenseCategory(name = "Health", color = HexColor("0xFFF87171"), iconKey = LucideIcons.Heart.name),
    ExpenseCategory(name ="Shopping", color = HexColor("0xFFFB923C"), iconKey = LucideIcons.ShoppingBag.name),
    ExpenseCategory(name = "Entertainment", color = HexColor("0xFF818CF8"), iconKey = LucideIcons.Tv.name)
)

val allExpenses = listOf(
    Expense(
        category = allCategories[0],
        date = LocalDate(2026, 8, 1),
        amount = 350.0,
        status = ExpenseStatus.CONFIRMED,
        source = ExpenseSource.MANUAL
    ),
    Expense(
        category = allCategories[0],
        date = LocalDate(2026, 8, 1),
        amount = 400.0,
        status = ExpenseStatus.CONFIRMED,
        source = ExpenseSource.MANUAL
    ),
    Expense(
        category = allCategories[1],
        date = LocalDate(2026, 8, 2),
        amount = 120.0,
        status = ExpenseStatus.CONFIRMED,
        source = ExpenseSource.MANUAL
    ),
    Expense(
        category = allCategories[2],
        date = LocalDate(2026, 8, 3),
        amount = 200.0,
        status = ExpenseStatus.CONFIRMED,
        source = ExpenseSource.MANUAL
    ),
    Expense(
        category = allCategories[3],
        date = LocalDate(2026, 8, 1),
        amount = 80.0,
        status = ExpenseStatus.CONFIRMED,
        source = ExpenseSource.MANUAL
    ),
    Expense(
        category = allCategories[3],
        date = LocalDate(2026, 8, 2),
        amount = 180.0,
        status = ExpenseStatus.CONFIRMED,
        source = ExpenseSource.MANUAL
    ),
    Expense(
        category = allCategories[3],
        date = LocalDate(2026, 7, 1),
        amount = 280.0,
        status = ExpenseStatus.CONFIRMED,
        source = ExpenseSource.MANUAL
    ),
    Expense(
        category = allCategories[2],
        date = LocalDate(2026, 7, 3),
        amount = 500.0,
        status = ExpenseStatus.CONFIRMED,
        source = ExpenseSource.MANUAL
    ),
    Expense(
        category = allCategories[2],
        date = LocalDate(2026, 9, 3),
        amount = 700.0,
        status = ExpenseStatus.PLANNED,
        source = ExpenseSource.MANUAL
    ),
)

val plannedExpenses = listOf(
    ExpenseItem(
        title = "Bricklayer's Labor",
        date = LocalDate(2026, 9, 9),
        amount = 4000.00,
        iconKey = LucideIcons.Wrench.name,
        iconColor = HexColor("0xFFEAB308") // Amarelo/Laranja
    ),
    ExpenseItem(
        title = "Custom Cabinets",
        date = LocalDate(2026, 9, 14),
        amount = 6000.00,
        iconKey = LucideIcons.ShoppingBag.name,
        iconColor = HexColor("0xFFEC4899") // Rosa
    ),
    ExpenseItem(
        title = "Airfare",
        date = LocalDate(2026, 9, 14),
        amount = 2500.00,
        iconKey = LucideIcons.Car.name,
        iconColor = HexColor("0xFF3B82F6") // Azul
    ),
    ExpenseItem(
        title = "Accommodation (guesthouse)",
        date = LocalDate(2026, 9, 19),
        amount = 3000.00,
        iconKey = LucideIcons.House.name,
        iconColor = HexColor("0xFF14B8A6") // Teal
    ),
    ExpenseItem(
        title = "Bathroom Fixtures",
        date = LocalDate(2026, 9, 30),
        amount = 1800.00,
        iconKey = LucideIcons.ShoppingBag.name,
        iconColor = HexColor("0xFFEC4899") // Rosa
    )
)

// Recent Expenses Card Data
val recentExpenses = listOf(
    ExpenseItem(
        title = "Amazon - Earbuds",
        date = LocalDate(2026, 8, 7),
        amount = 159.90,
        iconKey = LucideIcons.ShoppingBag.name,
        iconColor = HexColor("0xFFEC4899")
    ),
    ExpenseItem(
        title = "Gym SmartFit",
        date = LocalDate(2026, 8, 6),
        amount = 99.90,
        iconKey = LucideIcons.Activity.name,
        iconColor = HexColor("0xFF84CC16") // Verde Limão
    ),
    ExpenseItem(
        title = "Drogasil",
        date = LocalDate(2026, 8, 5),
        amount = 89.90,
        iconKey = LucideIcons.Heart.name,
        iconColor = HexColor("0xFF10B981") // Verde Esmeralda
    ),
    ExpenseItem(
        title = "Rent",
        date = LocalDate(2026, 8, 4),
        amount = 1800.00,
        iconKey = LucideIcons.House.name,
        iconColor = HexColor("0xFF14B8A6")
    ),
    ExpenseItem(
        title = "Netflix",
        date = LocalDate(2026, 8, 3),
        amount = 55.90,
        iconKey = LucideIcons.Gamepad.name,
        iconColor = HexColor("0xFF6366F1") // Roxo/Indigo
    )
)

/**
 * Returns the total amount of expenses for thd given [Expense] list
 */
fun List<Expense>.amountTotal(): Double = this.sumOf { e -> e.amount }

/**
 * Returns a list of expenses in [this] list which were [ExpenseStatus.CONFIRMED]
 */
fun List<Expense>.confirmed(): List<Expense> = this.filter { e -> e.status == ExpenseStatus.CONFIRMED }

/**
 * Returns a list of expenses in [this] list which occurred on the given [Month]
 */
fun List<Expense>.inMonth(month: Month): List<Expense> = this.filter { e -> e.date.month == month }

/**
 * Returns a [List] of [MonthlyExpensePerCategoryViewData] representation for the given list
 * of [Expense].
 */
fun List<Expense>.monthlyPerCategoryViewData() : List<MonthlyExpensePerCategoryViewData> {
    val totalAmount = this.sumOf { e -> e.amount }
    // Efficient single-pass summation
    val totalByCategory = this.groupingBy { it.category }
        .fold(0.0) { accumulator, element -> accumulator + element.amount }
    return totalByCategory.map { (category, sum) ->
        MonthlyExpensePerCategoryViewData(
            name = category.name,
            amount = sum,
            percentage = (sum / totalAmount) * 100.0,
            color = category.color.toComposeColor(),
            icon = category.iconKey.lucidIconVector()
        )
    }
}

fun monthlyExpensesData(month: Month): List<MonthlyExpensePerCategoryViewData> {
    // TODO: Go to fetch all expenses from local db
    return confirmedExpensesInMonth(month).monthlyPerCategoryViewData()
}

fun totalExpensesWithCurrencySymbol(symbol: String, month: Month): String {
    return "$symbol ${confirmedExpensesInMonth(month).amountTotal()}"
}

fun confirmedExpensesInMonth(month: Month): List<Expense> = allExpenses.confirmed().inMonth(month)
