package br.inf.cepp.financemanager.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import br.inf.cepp.financemanager.FinanceManager
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

// Notice the explicit 'private val' declaration here:
class AndroidPlatformUtils(private val appContext: Context) : PlatformUtils {

    override fun getCurrentCurrencySymbol(): String {
        return try {
            Currency.getInstance(Locale.getDefault()).symbol
        } catch (e: Exception) {
            "R$"
        }
    }

    override fun formatMonthYear(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        return date.toJavaLocalDate().format(formatter)
    }

    override fun triggerHapticFeedback() {
        // 2. Explicit reference prevents the "Function invocation expected" compiler loop
        val vibrator = this.appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(50)
        }
    }

    override fun log(message: String, tag: String?) {
        Log.d(tag?: FinanceManager.TAG, message)
    }

}