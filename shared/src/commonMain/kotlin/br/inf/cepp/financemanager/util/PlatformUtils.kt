package br.inf.cepp.financemanager.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

interface PlatformUtils {
    fun getCurrentCurrencySymbol(): String
    fun formatMonthYear(date: LocalDate): String
    fun triggerHapticFeedback()

    fun log(message: String, tag: String? = null)

}

