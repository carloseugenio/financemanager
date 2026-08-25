package br.inf.cepp.financemanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        alwaysOnTop = true,
        title = "FinanceManager",
    ) {
        val app = FinanceManager()
        app.Start()
    }
}