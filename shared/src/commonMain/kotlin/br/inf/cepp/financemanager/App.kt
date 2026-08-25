package br.inf.cepp.financemanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.inf.cepp.financemanager.model.monthlyExpensesData
import br.inf.cepp.financemanager.ui.screen.FinanceDashboardScreen
import br.inf.cepp.financemanager.ui.screen.PreviewAppTheme
import br.inf.cepp.financemanager.util.today

@Composable
@Preview
fun App() {
    PreviewAppTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FinanceDashboardScreen()
        }
    }
}
