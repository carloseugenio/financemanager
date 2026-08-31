package br.inf.cepp.financemanager

import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.pdf.generateMonthlyPdf
import br.inf.cepp.financemanager.pdf.generateProjectsPdf
import br.inf.cepp.financemanager.util.exportMonthlyToCsv
import br.inf.cepp.financemanager.util.exportProjectsToCsv
import br.inf.cepp.financemanager.ui.util.HexColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class ExporterTest {

    @Test
    fun projectsCsvProducesRowsForPlansAndItems() {
        val cat = ExpenseCategory("Misc", HexColor("#000000"), "icon")
        val plan = ProjectPlan(name = "Trip", startDate = LocalDate(2026, Month.SEPTEMBER, 1), endDate = LocalDate(2026, Month.SEPTEMBER, 30), budget = 3000.0, status = ProjectStatus.ACTIVE)
        val item = ProjectItem(id = 0L, planName = "Trip", category = cat, description = "Flight", budget = 700.0, actual = 0.0, expectedDate = LocalDate(2026, Month.SEPTEMBER, 5), relatedExpense = br.inf.cepp.financemanager.model.ExpenseItem(id=0, title="", date=LocalDate(2026, Month.SEPTEMBER,5), amount=0.0, iconKey="", iconColor=HexColor("#000000")))
        val csv = exportProjectsToCsv(listOf(plan), mapOf("Trip" to listOf(item)))
        assertTrue(csv.startsWith("Project Name,Start Date"))
        assertTrue(csv.contains("Trip"))
        assertTrue(csv.contains("Flight"))
    }

    @Test
    fun monthlyCsvContainsExpenseRows() {
        val cat = ExpenseCategory("Food", HexColor("#FF0000"), "cart")
        val exp = Expense(id = 1L, description = "Supermarket", category = cat, date = LocalDate(2026, Month.JANUARY, 12), amount = 120.0, status = ExpenseStatus.CONFIRMED, source = ExpenseSource.MANUAL)
        val csv = exportMonthlyToCsv(listOf(exp))
        val lines = csv.split('\n').filter { it.isNotBlank() }
        assertEquals(2, lines.size) // header + one row
        assertTrue(lines[1].contains("Supermarket"))
        assertTrue(lines[1].contains("120.0"))
    }

    @Test
    fun monthlyPdfHandlesManyExpenseLines() {
        val category = ExpenseCategory("Food", HexColor("#FF0000"), "cart")
        val expenses = (1..60).map { day ->
            Expense(
                id = day.toLong(),
                description = "Expense $day",
                category = category,
                date = LocalDate(2026, Month.JANUARY, (day - 1) % 28 + 1),
                amount = day.toDouble(),
                status = ExpenseStatus.CONFIRMED,
                source = ExpenseSource.MANUAL
            )
        }

        val pdf = generateMonthlyPdf(expenses)

        assertTrue(pdf.isNotEmpty())
    }

    @Test
    fun projectsPdfHandlesManyPlannedItems() {
        val category = ExpenseCategory("Travel", HexColor("#000000"), "plane")
        val plan = ProjectPlan(
            name = "Trip",
            startDate = LocalDate(2026, Month.SEPTEMBER, 1),
            endDate = LocalDate(2026, Month.SEPTEMBER, 30),
            budget = 3000.0,
            status = ProjectStatus.ACTIVE
        )
        val item = ExpenseItem(
            id = 0L,
            title = "Placeholder",
            date = LocalDate(2026, Month.SEPTEMBER, 1),
            amount = 0.0,
            iconKey = "plane",
            iconColor = HexColor("#000000")
        )
        val items = (1..60).map { index ->
            ProjectItem(
                id = index.toLong(),
                planName = plan.name,
                category = category,
                description = "Planned item $index",
                budget = index.toDouble(),
                actual = 0.0,
                expectedDate = LocalDate(2026, Month.SEPTEMBER, (index - 1) % 28 + 1),
                relatedExpense = item
            )
        }

        val pdf = generateProjectsPdf(listOf(plan), mapOf(plan.name to items))

        assertTrue(pdf.isNotEmpty())
    }
}
