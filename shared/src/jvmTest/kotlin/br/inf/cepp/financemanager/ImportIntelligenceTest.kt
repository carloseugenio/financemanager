package br.inf.cepp.financemanager

import br.inf.cepp.financemanager.domain.ImportIntelligenceCalculator
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.model.ExpenseSource
import br.inf.cepp.financemanager.model.ExpenseStatus
import br.inf.cepp.financemanager.ui.util.HexColor
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ImportIntelligenceTest {
    private val calculator = ImportIntelligenceCalculator()

    @Test
    fun importSummaryFlagsDuplicatesAndSuggestions() {
        val groceries = ExpenseCategory("Groceries", HexColor("#F59E0B"), "shopping-cart")
        val transport = ExpenseCategory("Transport", HexColor("#3B82F6"), "car")
        val confirmed = listOf(
            Expense(
                id = 1,
                description = "Market run",
                category = groceries,
                date = LocalDate(2026, 8, 2),
                amount = 214.35,
                status = ExpenseStatus.CONFIRMED,
                source = ExpenseSource.MANUAL
            )
        )
        val drafts = listOf(
            Expense(
                id = 2,
                description = "Market run!!!",
                category = groceries,
                date = LocalDate(2026, 8, 3),
                amount = 214.35,
                status = ExpenseStatus.DRAFT,
                source = ExpenseSource.IMPORT
            ),
            Expense(
                id = 3,
                description = "Uber trip",
                category = transport,
                date = LocalDate(2026, 8, 4),
                amount = 38.40,
                status = ExpenseStatus.DRAFT,
                source = ExpenseSource.IMPORT
            )
        )

        val summary = calculator.summarizeDraftImports(drafts, confirmed, listOf(groceries, transport))

        assertEquals(2, summary.totalDrafts)
        assertEquals(1, summary.duplicateCount)
        val duplicateItem = summary.previewItems.first { it.normalizedMerchant == "market run" }
        val transportItem = summary.previewItems.first { it.normalizedMerchant == "uber trip" }

        assertEquals("market run", duplicateItem.normalizedMerchant)
        assertNotNull(duplicateItem.duplicateMatch)
        assertTrue(transportItem.suggestedCategory?.name == "Transport")
        assertTrue(transportItem.confidencePercent >= 70.0)
    }

    @Test
    fun normalizeMerchantRemovesNoise() {
        val value = calculator.normalizeMerchant("  UBER *Trip #1234  ")

        assertEquals("uber trip 1234", value)
    }
}
