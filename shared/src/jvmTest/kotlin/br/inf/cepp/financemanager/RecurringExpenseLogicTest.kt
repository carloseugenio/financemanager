package br.inf.cepp.financemanager

import br.inf.cepp.financemanager.model.*
import br.inf.cepp.financemanager.ui.util.HexColor
import br.inf.cepp.financemanager.scheduler.RecurringExpenseScheduleUtils
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Targeted tests for recurring expense logic: due-date calculation and duplicate prevention.
 * Tests the core materialization logic that will run in RecurringExpenseWorker.
 */
class RecurringExpenseLogicTest {

    private val testCategory = ExpenseCategory("Food", HexColor("#FF9800"), "cart")

    // ========== Due-Date Calculation Tests ==========

    @Test
    fun dailyRecurrenceCalculatesDueDatesCorrectly() {
        val startDate = LocalDate(2026, Month.JANUARY, 1)
        val rule = RecurrenceRule(
            frequency = RecurrenceFrequency.DAILY,
            interval = 2,
            count = 3
        )

        // Should be due on Jan 1, 3, 5, etc.
        assertTrue(isDueOnDate(startDate, startDate, rule), "Jan 1 should be due")
        assertTrue(isDueOnDate(startDate, LocalDate(2026, Month.JANUARY, 3), rule), "Jan 3 should be due (2-day interval)")
        assertFalse(isDueOnDate(startDate, LocalDate(2026, Month.JANUARY, 2), rule), "Jan 2 should not be due")
        assertTrue(isDueOnDate(startDate, LocalDate(2026, Month.JANUARY, 5), rule), "Jan 5 should be due")
    }

    @Test
    fun weeklyRecurrenceCalculatesDueDatesCorrectly() {
        val startDate = LocalDate(2026, Month.JANUARY, 1) // Thursday
        val rule = RecurrenceRule(
            frequency = RecurrenceFrequency.WEEKLY,
            interval = 1,
            count = 4
        )

        // Should be due on same day of week (Thursdays)
        assertTrue(isDueOnDate(startDate, startDate, rule), "Jan 1 (Thu) should be due")
        assertTrue(isDueOnDate(startDate, LocalDate(2026, Month.JANUARY, 8), rule), "Jan 8 (Thu) should be due")
        assertFalse(isDueOnDate(startDate, LocalDate(2026, Month.JANUARY, 2), rule), "Jan 2 (Fri) should not be due")
        assertTrue(isDueOnDate(startDate, LocalDate(2026, Month.JANUARY, 15), rule), "Jan 15 (Thu) should be due")
    }

    @Test
    fun monthlyRecurrenceCalculatesDueDatesCorrectly() {
        val startDate = LocalDate(2026, Month.JANUARY, 15)
        val rule = RecurrenceRule(
            frequency = RecurrenceFrequency.MONTHLY,
            interval = 1,
            count = 4
        )

        // Should be due on the 15th of each month
        assertTrue(isDueOnDate(startDate, startDate, rule), "Jan 15 should be due")
        assertTrue(isDueOnDate(startDate, LocalDate(2026, Month.FEBRUARY, 15), rule), "Feb 15 should be due")
        assertFalse(isDueOnDate(startDate, LocalDate(2026, Month.FEBRUARY, 14), rule), "Feb 14 should not be due")
        assertTrue(isDueOnDate(startDate, LocalDate(2026, Month.MARCH, 15), rule), "Mar 15 should be due")
    }

    @Test
    fun monthlyRecurrenceChecksDayOfMonth() {
        // Jan 31 + 1 month: in simple recurrence, Feb 28 is NOT considered due (day mismatch: 28 != 31)
        // This is a known limitation; proper calendar arithmetic is complex
        val startDate = LocalDate(2026, Month.JANUARY, 31)
        val rule = RecurrenceRule(
            frequency = RecurrenceFrequency.MONTHLY,
            interval = 1
        )

        assertTrue(isDueOnDate(startDate, startDate, rule), "Jan 31 should be due")
        // Feb 28 is NOT due because the day of month doesn't match (28 != 31)
        assertFalse(isDueOnDate(startDate, LocalDate(2026, Month.FEBRUARY, 28), rule), "Feb 28 should not be due (day mismatch)")
        // Next "due" date would be Mar 31
        assertTrue(isDueOnDate(startDate, LocalDate(2026, Month.MARCH, 31), rule), "Mar 31 should be due (day matches)")
    }

    @Test
    fun yearlyRecurrenceCalculatesDueDatesCorrectly() {
        val startDate = LocalDate(2026, Month.MARCH, 15)
        val rule = RecurrenceRule(
            frequency = RecurrenceFrequency.YEARLY,
            interval = 1,
            count = 3
        )

        // Should be due on Mar 15 every year
        assertTrue(isDueOnDate(startDate, startDate, rule), "2026-03-15 should be due")
        assertTrue(isDueOnDate(startDate, LocalDate(2027, Month.MARCH, 15), rule), "2027-03-15 should be due")
        assertFalse(isDueOnDate(startDate, LocalDate(2027, Month.MARCH, 14), rule), "2027-03-14 should not be due")
        assertTrue(isDueOnDate(startDate, LocalDate(2028, Month.MARCH, 15), rule), "2028-03-15 should be due")
    }

    @Test
    fun recurrenceRespectCountLimit() {
        val startDate = LocalDate(2026, Month.JANUARY, 1)
        val rule = RecurrenceRule(
            frequency = RecurrenceFrequency.DAILY,
            interval = 1,
            count = 3
        )

        assertTrue(isWithinOccurrenceLimit(startDate, LocalDate(2026, Month.JANUARY, 1), rule), "Occurrence 1 should be within limit")
        assertTrue(isWithinOccurrenceLimit(startDate, LocalDate(2026, Month.JANUARY, 2), rule), "Occurrence 2 should be within limit")
        assertTrue(isWithinOccurrenceLimit(startDate, LocalDate(2026, Month.JANUARY, 3), rule), "Occurrence 3 should be within limit (at limit)")
        assertFalse(isWithinOccurrenceLimit(startDate, LocalDate(2026, Month.JANUARY, 4), rule), "Occurrence 4 should exceed limit")
    }

    @Test
    fun recurrenceRespectUntilDate() {
        val startDate = LocalDate(2026, Month.JANUARY, 1)
        val until = LocalDate(2026, Month.JANUARY, 10)
        val rule = RecurrenceRule(
            frequency = RecurrenceFrequency.DAILY,
            interval = 1,
            until = until
        )

        assertTrue(isDueOnDate(startDate, LocalDate(2026, Month.JANUARY, 5), rule), "Jan 5 should be due (before until)")
        assertTrue(isDueOnDate(startDate, LocalDate(2026, Month.JANUARY, 10), rule), "Jan 10 should be due (at until)")
        assertFalse(isDueOnDate(startDate, LocalDate(2026, Month.JANUARY, 11), rule), "Jan 11 should not be due (after until)")
    }

    @Test
    fun nextRunDateOnOrAfterUsesStartDateWhenFuture() {
        val startDate = LocalDate(2026, Month.JANUARY, 20)
        val rule = RecurrenceRule(
            frequency = RecurrenceFrequency.WEEKLY,
            interval = 1
        )

        assertEquals(
            startDate,
            RecurringExpenseScheduleUtils.nextRunDateOnOrAfter(
                startDate = startDate,
                currentDate = LocalDate(2026, Month.JANUARY, 10),
                rule = rule
            )
        )
    }

    @Test
    fun nextRunDateOnOrAfterAdvancesToNextOccurrence() {
        val startDate = LocalDate(2026, Month.JANUARY, 1)
        val rule = RecurrenceRule(
            frequency = RecurrenceFrequency.MONTHLY,
            interval = 1
        )

        assertEquals(
            LocalDate(2026, Month.FEBRUARY, 1),
            RecurringExpenseScheduleUtils.nextRunDateOnOrAfter(
                startDate = startDate,
                currentDate = LocalDate(2026, Month.JANUARY, 20),
                rule = rule
            )
        )
    }

    // ========== Duplicate Prevention Tests ==========

    @Test
    fun hasMaterializedExpenseDetectsExactDuplicate() {
        val baseExpense = Expense(
            id = 1L,
            description = "Rent",
            category = testCategory,
            date = LocalDate(2026, Month.JANUARY, 1),
            amount = 1000.0,
            status = ExpenseStatus.PLANNED,
            source = ExpenseSource.MANUAL,
            recurrence = RecurrenceRule()
        )

        val confirmed = Expense(
            id = 2L,
            description = "Materialized Rent",
            category = testCategory,
            date = LocalDate(2026, Month.JANUARY, 1),
            amount = 1000.0,
            status = ExpenseStatus.CONFIRMED,
            source = ExpenseSource.MANUAL
        )

        assertTrue(
            hasMaterializedForDate(listOf(confirmed), baseExpense, LocalDate(2026, Month.JANUARY, 1)),
            "Should detect duplicate on same source/date/category"
        )
    }

    @Test
    fun hasMaterializedExpensePreventsIdempotentDuplication() {
        val baseExpense = Expense(
            id = 1L,
            description = "Subscription",
            category = testCategory,
            date = LocalDate(2026, Month.JANUARY, 1),
            amount = 50.0,
            status = ExpenseStatus.PLANNED,
            source = ExpenseSource.IMPORT,
            recurrence = RecurrenceRule()
        )

        val alreadyMaterialized = Expense(
            id = 2L,
            description = "Any description",
            category = testCategory,
            date = LocalDate(2026, Month.JANUARY, 1),
            amount = 999.0, // Different amount
            status = ExpenseStatus.CONFIRMED,
            source = ExpenseSource.IMPORT // Same source
        )

        // Should detect based on (source, date, category), not on amount or description
        assertTrue(
            hasMaterializedForDate(listOf(alreadyMaterialized), baseExpense, LocalDate(2026, Month.JANUARY, 1)),
            "Should detect duplicate regardless of amount/description differences"
        )
    }

    @Test
    fun hasMaterializedExpenseIgnoresDifferentDate() {
        val baseExpense = Expense(
            id = 1L,
            description = "Utilities",
            category = testCategory,
            date = LocalDate(2026, Month.JANUARY, 1),
            amount = 100.0,
            status = ExpenseStatus.PLANNED,
            source = ExpenseSource.MANUAL,
            recurrence = RecurrenceRule()
        )

        val differentDateExpense = Expense(
            id = 2L,
            description = "Utilities",
            category = testCategory,
            date = LocalDate(2026, Month.JANUARY, 2), // Different date
            amount = 100.0,
            status = ExpenseStatus.CONFIRMED,
            source = ExpenseSource.MANUAL
        )

        assertFalse(
            hasMaterializedForDate(listOf(differentDateExpense), baseExpense, LocalDate(2026, Month.JANUARY, 1)),
            "Should not detect duplicate when date differs"
        )
    }

    @Test
    fun hasMaterializedExpenseIgnoresDifferentSource() {
        val baseExpense = Expense(
            id = 1L,
            description = "Food",
            category = testCategory,
            date = LocalDate(2026, Month.JANUARY, 1),
            amount = 50.0,
            status = ExpenseStatus.PLANNED,
            source = ExpenseSource.MANUAL,
            recurrence = RecurrenceRule()
        )

        val differentSourceExpense = Expense(
            id = 2L,
            description = "Food",
            category = testCategory,
            date = LocalDate(2026, Month.JANUARY, 1),
            amount = 50.0,
            status = ExpenseStatus.CONFIRMED,
            source = ExpenseSource.SMS // Different source
        )

        assertFalse(
            hasMaterializedForDate(listOf(differentSourceExpense), baseExpense, LocalDate(2026, Month.JANUARY, 1)),
            "Should not detect duplicate when source differs"
        )
    }

    @Test
    fun multipleExpensesOnlyDetectRightDuplicate() {
        val baseExpense = Expense(
            id = 1L,
            description = "Gym",
            category = testCategory,
            date = LocalDate(2026, Month.JANUARY, 1),
            amount = 50.0,
            status = ExpenseStatus.PLANNED,
            source = ExpenseSource.IMPORT,
            recurrence = RecurrenceRule()
        )

        val confirmed = listOf(
            Expense(id = 2L, description = "Gym Jan", category = testCategory, date = LocalDate(2026, Month.DECEMBER, 1), amount = 50.0, status = ExpenseStatus.CONFIRMED, source = ExpenseSource.IMPORT),
            Expense(id = 3L, description = "Gym Jan", category = testCategory, date = LocalDate(2026, Month.JANUARY, 1), amount = 50.0, status = ExpenseStatus.CONFIRMED, source = ExpenseSource.IMPORT),
            Expense(id = 4L, description = "Gym Feb", category = testCategory, date = LocalDate(2026, Month.JANUARY, 1), amount = 50.0, status = ExpenseStatus.CONFIRMED, source = ExpenseSource.SMS),
        )

        assertTrue(
            hasMaterializedForDate(confirmed, baseExpense, LocalDate(2026, Month.JANUARY, 1)),
            "Should detect the correct duplicate (id=3) by (source, date, category)"
        )
    }

    // ========== Helper Methods (Mirror Worker Logic) ==========

    private fun isDueOnDate(
        startDate: LocalDate,
        targetDate: LocalDate,
        rule: RecurrenceRule
    ): Boolean {
        if (rule.interval <= 0) return false
        if (targetDate < startDate) return false
        if (rule.until != null && targetDate > rule.until) return false

        return when (rule.frequency) {
            RecurrenceFrequency.DAILY -> {
                val daysBetween = (targetDate.toEpochDays() - startDate.toEpochDays()).toLong()
                daysBetween >= 0L && daysBetween % rule.interval.toLong() == 0L
            }
            RecurrenceFrequency.WEEKLY -> {
                val daysBetween = (targetDate.toEpochDays() - startDate.toEpochDays()).toLong()
                daysBetween >= 0L &&
                        targetDate.dayOfWeek == startDate.dayOfWeek &&
                        daysBetween % (7L * rule.interval.toLong()) == 0L
            }
            RecurrenceFrequency.MONTHLY -> {
                val monthsBetween =
                    ((targetDate.year - startDate.year) * 12) +
                            (targetDate.monthNumber - startDate.monthNumber)
                monthsBetween >= 0 &&
                        monthsBetween % rule.interval == 0 &&
                        targetDate.dayOfMonth == startDate.dayOfMonth
            }
            RecurrenceFrequency.YEARLY -> {
                val yearsBetween = targetDate.year - startDate.year
                yearsBetween >= 0 &&
                        yearsBetween % rule.interval == 0 &&
                        targetDate.month == startDate.month &&
                        targetDate.dayOfMonth == startDate.dayOfMonth
            }
        }
    }

    private fun isWithinOccurrenceLimit(
        startDate: LocalDate,
        targetDate: LocalDate,
        rule: RecurrenceRule
    ): Boolean {
        val count = rule.count ?: return true
        if (count <= 0) return false
        return occurrenceNumber(startDate, targetDate, rule) <= count
    }

    private fun occurrenceNumber(
        startDate: LocalDate,
        targetDate: LocalDate,
        rule: RecurrenceRule
    ): Int {
        if (targetDate < startDate) return 0
        return when (rule.frequency) {
            RecurrenceFrequency.DAILY -> {
                val daysBetween = targetDate.toEpochDays() - startDate.toEpochDays()
                ((daysBetween / rule.interval) + 1).toInt()
            }
            RecurrenceFrequency.WEEKLY -> {
                val daysBetween = targetDate.toEpochDays() - startDate.toEpochDays()
                ((daysBetween / (7 * rule.interval)) + 1).toInt()
            }
            RecurrenceFrequency.MONTHLY -> {
                val monthsBetween =
                    ((targetDate.year - startDate.year) * 12) +
                            (targetDate.monthNumber - startDate.monthNumber)
                ((monthsBetween / rule.interval) + 1).toInt()
            }
            RecurrenceFrequency.YEARLY -> {
                val yearsBetween = targetDate.year - startDate.year
                ((yearsBetween / rule.interval) + 1).toInt()
            }
        }
    }

    private fun hasMaterializedForDate(
        confirmedExpenses: List<Expense>,
        baseExpense: Expense,
        targetDate: LocalDate
    ): Boolean {
        return confirmedExpenses.any { confirmed ->
            confirmed.source == baseExpense.source &&
                    confirmed.date == targetDate &&
                    confirmed.category == baseExpense.category
        }
    }
}
