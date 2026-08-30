package br.inf.cepp.financemanager

import br.inf.cepp.financemanager.model.*
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
}
