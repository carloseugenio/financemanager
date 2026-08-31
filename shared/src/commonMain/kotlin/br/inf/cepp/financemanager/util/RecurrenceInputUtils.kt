package br.inf.cepp.financemanager.util

import br.inf.cepp.financemanager.model.RecurrenceFrequency
import br.inf.cepp.financemanager.model.RecurrenceRule
import br.inf.cepp.financemanager.model.Reminder
import br.inf.cepp.financemanager.model.ReminderMethod

object RecurrenceInputUtils {
    fun parsePositiveInt(raw: String): Int? {
        return raw.trim()
            .takeIf { it.isNotEmpty() }
            ?.toIntOrNull()
            ?.takeIf { it > 0 }
    }

    fun buildRecurrenceRule(
        enabled: Boolean,
        frequency: RecurrenceFrequency,
        intervalText: String,
        countText: String,
        untilText: String,
        language: String
    ): RecurrenceRule? {
        if (!enabled) return null

        val interval = parsePositiveInt(intervalText) ?: return null
        val count = parsePositiveInt(countText)
        val normalizedUntil = untilText.trim()
        val until = if (normalizedUntil.isEmpty()) {
            null
        } else {
            DateInputUtils.parseLenient(normalizedUntil, language) ?: return null
        }

        return RecurrenceRule(
            frequency = frequency,
            interval = interval,
            count = count,
            until = until
        )
    }

    fun buildReminder(
        enabled: Boolean,
        method: ReminderMethod,
        leadTimeText: String,
        destinationText: String
    ): Reminder? {
        if (!enabled) return null

        val leadTime = leadTimeText.trim()
            .toLongOrNull()
            ?.takeIf { it >= 0 }
            ?: return null
        val destination = destinationText.trim().takeIf { it.isNotEmpty() }
        if ((method == ReminderMethod.SMS || method == ReminderMethod.EMAIL) && destination == null) {
            return null
        }

        return Reminder(
            enabled = true,
            method = method,
            leadTimeMinutes = leadTime,
            destination = destination
        )
    }

    fun describeRecurrence(rule: RecurrenceRule, language: String): String {
        val unit = when (rule.frequency) {
            RecurrenceFrequency.DAILY -> "day"
            RecurrenceFrequency.WEEKLY -> "week"
            RecurrenceFrequency.MONTHLY -> "month"
            RecurrenceFrequency.YEARLY -> "year"
        }
        val cadence = if (rule.interval == 1) {
            "Every $unit"
        } else {
            "Every ${rule.interval} ${unit}s"
        }

        val details = buildList {
            rule.count?.let { add("count $it") }
            rule.until?.let { add("until ${DateInputUtils.formatForLocale(it, language)}") }
        }

        return if (details.isEmpty()) cadence else "$cadence (${details.joinToString(", ")})"
    }

    fun describeReminder(reminder: Reminder): String {
        val leadTime = if (reminder.leadTimeMinutes == 0L) {
            "at due time"
        } else {
            "${reminder.leadTimeMinutes} minutes before"
        }
        val destination = reminder.destination?.takeIf { it.isNotBlank() }
        val target = when (reminder.method) {
            ReminderMethod.NOTIFICATION -> "notification"
            ReminderMethod.ALARM -> "alarm"
            ReminderMethod.SMS -> "SMS" + destination?.let { " to $it" }.orEmpty()
            ReminderMethod.EMAIL -> "email" + destination?.let { " to $it" }.orEmpty()
        }
        return "Reminder by $target $leadTime"
    }
}
