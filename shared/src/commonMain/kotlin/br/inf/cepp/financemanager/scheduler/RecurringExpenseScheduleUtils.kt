package br.inf.cepp.financemanager.scheduler

import br.inf.cepp.financemanager.model.Expense
import br.inf.cepp.financemanager.model.RecurrenceFrequency
import br.inf.cepp.financemanager.model.RecurrenceRule
import kotlinx.datetime.LocalDate

object RecurringExpenseScheduleUtils {
    fun isDueOn(
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

    fun recurrenceOccurrenceNumber(
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

    fun isWithinOccurrenceCount(
        startDate: LocalDate,
        targetDate: LocalDate,
        rule: RecurrenceRule
    ): Boolean {
        val count = rule.count ?: return true
        if (count <= 0) return false
        return recurrenceOccurrenceNumber(startDate, targetDate, rule) <= count
    }

    fun computeNextRunDate(
        lastDate: LocalDate,
        rule: RecurrenceRule
    ): LocalDate {
        return when (rule.frequency) {
            RecurrenceFrequency.DAILY -> LocalDate.fromEpochDays(lastDate.toEpochDays() + rule.interval)
            RecurrenceFrequency.WEEKLY -> LocalDate.fromEpochDays(lastDate.toEpochDays() + (7L * rule.interval))
            RecurrenceFrequency.MONTHLY -> addMonthsToDate(lastDate, rule.interval)
            RecurrenceFrequency.YEARLY -> {
                val day = minOf(lastDate.dayOfMonth, getDaysInMonth(lastDate.monthNumber, lastDate.year + rule.interval))
                LocalDate(
                    lastDate.year + rule.interval,
                    lastDate.monthNumber,
                    day
                )
            }
        }
    }

    fun nextRunDateOnOrAfter(
        startDate: LocalDate,
        currentDate: LocalDate,
        rule: RecurrenceRule
    ): LocalDate? {
        if (rule.interval <= 0) return null
        if (rule.until != null && currentDate > rule.until) return null
        if (currentDate < startDate) return startDate

        var candidate = startDate
        var safety = 0
        while (candidate < currentDate && safety < 512) {
            candidate = computeNextRunDate(candidate, rule)
            if (rule.until != null && candidate > rule.until) return null
            if (rule.count != null && recurrenceOccurrenceNumber(startDate, candidate, rule) > rule.count) return null
            safety++
        }

        return if (candidate >= currentDate) candidate else null
    }

    fun hasMaterializedExpenseForDate(
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

    private fun addMonthsToDate(date: LocalDate, months: Int): LocalDate {
        var newMonth = date.monthNumber + months
        var newYear = date.year

        while (newMonth > 12) {
            newMonth -= 12
            newYear++
        }

        val daysInMonth = getDaysInMonth(newMonth, newYear)
        val newDay = minOf(date.dayOfMonth, daysInMonth)
        return LocalDate(newYear, newMonth, newDay)
    }

    private fun getDaysInMonth(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 28
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}
