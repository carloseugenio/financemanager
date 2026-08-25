package br.inf.cepp.financemanager

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class FinanceManager {

    companion object {
        const val TAG: String = "FINANCE_MANAGER"
    }

    @Preview
    @Composable
    fun Start() {
        App()
    }
}