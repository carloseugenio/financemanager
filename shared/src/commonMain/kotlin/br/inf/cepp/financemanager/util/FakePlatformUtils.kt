package br.inf.cepp.financemanager.util

import br.inf.cepp.financemanager.FinanceManager
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.yearMonth
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale


class FakePlatformUtils(
    var mockCurrencySymbol: String = "$",
    var mockFormattedDate: String = "August 2026"
) : PlatformUtils {

    var hapticFeedbackCallCount = 0
        private set

    override fun getCurrentCurrencySymbol(): String {
        // Get the system's current default locale
        val currentLocale = Locale.getDefault()
        // Get the currency instance for that locale
        val currency = Currency.getInstance(currentLocale)
        // Get the symbol (e.g., $, €, R$)
        return currency.symbol
    }

    override fun formatMonthYear(date: LocalDate): String {
        // Define format
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.US)
        // Current month and year
        val current = Date()
        // Format to String
        val formatted: String? = sdf.format(current)
        return formatted?:mockFormattedDate
    }

    override fun triggerHapticFeedback() {
        hapticFeedbackCallCount++ // Tracks if your button click actually fired haptics
    }

    override fun log(message: String, tag: String?) {
        println("[${tag?: FinanceManager.TAG}]: $message")
    }

}