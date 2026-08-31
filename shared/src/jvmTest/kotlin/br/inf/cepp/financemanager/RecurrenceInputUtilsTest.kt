package br.inf.cepp.financemanager

import br.inf.cepp.financemanager.model.RecurrenceFrequency
import br.inf.cepp.financemanager.model.ReminderMethod
import br.inf.cepp.financemanager.util.RecurrenceInputUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecurrenceInputUtilsTest {
    @Test
    fun buildsRecurrenceRuleFromValidInputs() {
        val rule = RecurrenceInputUtils.buildRecurrenceRule(
            enabled = true,
            frequency = RecurrenceFrequency.MONTHLY,
            intervalText = "2",
            countText = "5",
            untilText = "2026-12-31",
            language = "en"
        )

        assertEquals(RecurrenceFrequency.MONTHLY, rule?.frequency)
        assertEquals(2, rule?.interval)
        assertEquals(5, rule?.count)
        assertEquals("2026-12-31", rule?.until.toString())
    }

    @Test
    fun rejectsInvalidRecurrenceInterval() {
        val rule = RecurrenceInputUtils.buildRecurrenceRule(
            enabled = true,
            frequency = RecurrenceFrequency.WEEKLY,
            intervalText = "0",
            countText = "",
            untilText = "",
            language = "en"
        )

        assertNull(rule)
    }

    @Test
    fun buildsReminderForNotificationWithoutDestination() {
        val reminder = RecurrenceInputUtils.buildReminder(
            enabled = true,
            method = ReminderMethod.NOTIFICATION,
            leadTimeText = "30",
            destinationText = ""
        )

        assertEquals(ReminderMethod.NOTIFICATION, reminder?.method)
        assertEquals(30L, reminder?.leadTimeMinutes)
        assertNull(reminder?.destination)
    }

    @Test
    fun rejectsSmsReminderWithoutDestination() {
        val reminder = RecurrenceInputUtils.buildReminder(
            enabled = true,
            method = ReminderMethod.SMS,
            leadTimeText = "30",
            destinationText = ""
        )

        assertNull(reminder)
    }
}
