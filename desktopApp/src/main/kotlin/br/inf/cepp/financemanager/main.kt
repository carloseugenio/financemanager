package br.inf.cepp.financemanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
//import br.inf.cepp.financemanager.di.initKoin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        alwaysOnTop = true,
        title = "FinanceManager",
    ) {
        // 1. Initialize Koin graph first
//        initKoin()

            // 2. Start your orchestrator workflow
        FinanceManager().Start()
    }
}

fun mainOld() = application {
    Window(
        onCloseRequest = ::exitApplication,
        alwaysOnTop = true,
        title = "FinanceManager",
    ) {
        FinanceManager().Start()
    }
}
