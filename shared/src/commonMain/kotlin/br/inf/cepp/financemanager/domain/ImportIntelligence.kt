package br.inf.cepp.financemanager.domain

import br.inf.cepp.financemanager.model.Account
import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.model.ExpenseSource
import br.inf.cepp.financemanager.model.ExpenseStatus
import kotlin.math.abs

data class ImportReviewItem(
    val expense: Expense,
    val normalizedMerchant: String,
    val suggestedCategory: ExpenseCategory?,
    val confidencePercent: Double,
    val duplicateMatch: Expense?,
)

data class ImportHistoryItem(
    val expense: Expense,
    val normalizedMerchant: String,
)

data class ImportReviewSummary(
    val previewItems: List<ImportReviewItem>,
    val historyItems: List<ImportHistoryItem>,
    val totalDrafts: Int,
    val duplicateCount: Int,
    val suggestedCategoryCount: Int,
    val normalizedMerchantCount: Int,
)

class ImportIntelligenceCalculator {
    fun summarizeDraftImports(
        drafts: List<Expense>,
        confirmedExpenses: List<Expense>,
        categories: List<ExpenseCategory>,
    ): ImportReviewSummary {
        val previewItems = drafts
            .sortedWith(compareByDescending<Expense> { it.date }.thenByDescending { it.id })
            .map { expense ->
                val normalizedMerchant = normalizeMerchant(expense.description)
                val duplicateMatch = confirmedExpenses.firstOrNull { candidate ->
                    candidate.status == ExpenseStatus.CONFIRMED &&
                        normalizeMerchant(candidate.description) == normalizedMerchant &&
                        amountsMatch(expense.amount, candidate.amount) &&
                        datesAreClose(expense, candidate)
                }
                val suggestedCategory = suggestCategory(expense.description, categories)
                val confidencePercent = calculateConfidence(normalizedMerchant, suggestedCategory, duplicateMatch)

                ImportReviewItem(
                    expense = expense,
                    normalizedMerchant = normalizedMerchant,
                    suggestedCategory = suggestedCategory,
                    confidencePercent = confidencePercent,
                    duplicateMatch = duplicateMatch,
                )
            }

        val historyItems = confirmedExpenses
            .filter { it.source != ExpenseSource.MANUAL }
            .sortedWith(compareByDescending<Expense> { it.date }.thenByDescending { it.id })
            .take(5)
            .map { expense ->
                ImportHistoryItem(
                    expense = expense,
                    normalizedMerchant = normalizeMerchant(expense.description),
                )
            }

        return ImportReviewSummary(
            previewItems = previewItems,
            historyItems = historyItems,
            totalDrafts = previewItems.size,
            duplicateCount = previewItems.count { it.duplicateMatch != null },
            suggestedCategoryCount = previewItems.count { it.suggestedCategory != null },
            normalizedMerchantCount = previewItems.map { it.normalizedMerchant }.distinct().size,
        )
    }

    fun normalizeMerchant(value: String): String {
        return value
            .lowercase()
            .replace(merchantNoise, " ")
            .replace(duplicateWhitespace, " ")
            .trim()
    }

    private fun suggestCategory(description: String, categories: List<ExpenseCategory>): ExpenseCategory? {
        if (categories.isEmpty()) return null

        val normalizedDescription = normalizeMerchant(description)
        val tokens = normalizedDescription.split(" ").filter { it.isNotBlank() }.toSet()

        return categories
            .map { category -> category to scoreCategory(category, tokens, normalizedDescription) }
            .filter { it.second > 0 }
            .sortedWith(compareByDescending<Pair<ExpenseCategory, Int>> { it.second }.thenBy { it.first.name })
            .firstOrNull()
            ?.first
    }

    private fun scoreCategory(category: ExpenseCategory, merchantTokens: Set<String>, normalizedDescription: String): Int {
        val normalizedCategory = normalizeMerchant(category.name)
        val categoryTokens = normalizedCategory.split(" ").filter { it.isNotBlank() }
        var score = 0

        if (categoryTokens.any { merchantTokens.contains(it) }) {
            score += 3
        }

        val signals = categorySignals[normalizedCategory] ?: emptySet()
        signals.forEach { signal ->
            if (merchantTokens.contains(signal)) {
                score += 2
            }
            if (normalizedDescription.contains(signal)) {
                score += 1
            }
        }

        return score
    }

    private fun calculateConfidence(
        normalizedMerchant: String,
        suggestedCategory: ExpenseCategory?,
        duplicateMatch: Expense?,
    ): Double {
        if (duplicateMatch != null) return 100.0
        if (suggestedCategory != null) {
            return when {
                normalizedMerchant.isBlank() -> 45.0
                normalizedMerchant.length >= 12 -> 82.0
                else -> 70.0
            }
        }
        return if (normalizedMerchant.isBlank()) 35.0 else 55.0
    }

    private fun datesAreClose(left: Expense, right: Expense): Boolean {
        return abs(left.date.toEpochDays() - right.date.toEpochDays()) <= 3
    }

    private fun amountsMatch(left: Double, right: Double): Boolean {
        return abs(left - right) <= 0.01
    }

    private companion object {
        val merchantNoise = Regex("[^\\p{L}\\p{Nd}]+")
        val duplicateWhitespace = Regex("\\s+")
        val categorySignals = mapOf(
            "groceries" to setOf("market", "supermarket", "grocery", "groceries", "mercado", "supermercado", "food"),
            "transport" to setOf("uber", "taxi", "bus", "train", "metro", "fuel", "gas", "ride", "parking"),
            "health" to setOf("pharmacy", "drugstore", "clinic", "doctor", "hospital", "health", "medicine"),
            "subscriptions" to setOf("subscription", "renewal", "membership", "streaming", "netflix", "spotify", "prime"),
        )
    }
}
