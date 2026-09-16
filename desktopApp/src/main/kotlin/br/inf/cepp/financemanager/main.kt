package br.inf.cepp.financemanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import br.inf.cepp.financemanager.scheduler.DesktopRecurringExpenseScheduler

private fun installExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
}

fun main() {
    installExceptionHandler()
    DesktopRecurringExpenseScheduler().start()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            alwaysOnTop = true,
            title = "FinanceManager",
        ) {
            FinanceManager().Start(forceDarkTheme = true)
        }
    }
}
