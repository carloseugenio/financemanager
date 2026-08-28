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
import br.inf.cepp.financemanager.di.appModule
import br.inf.cepp.financemanager.ui.screen.MainAppNavigation
import br.inf.cepp.financemanager.ui.screen.PreviewAppTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

class FinanceManager {

    companion object {
        const val TAG: String = "FINANCE_MANAGER"
    }

    @Preview
    @Composable
    fun Start() {
        // 🚀 Load the direct module variable directly inside your configuration block
        val koinConfig = koinConfiguration {
            modules(appModule)
        }

        KoinApplication(configuration = koinConfig) {
            PreviewAppTheme {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .safeContentPadding()
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MainAppNavigation()
                }
            }
        }
    }
}
