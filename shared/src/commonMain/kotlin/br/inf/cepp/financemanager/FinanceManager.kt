package br.inf.cepp.financemanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.inf.cepp.financemanager.di.appModule
import br.inf.cepp.financemanager.repository.DatabaseInitializer
import br.inf.cepp.financemanager.ui.screen.MainAppNavigation
import br.inf.cepp.financemanager.ui.screen.PreviewAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.getKoin
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
            val koin = getKoin()
            LaunchedEffect(Unit) {
                val initializer = koin.get<DatabaseInitializer>()
                initializer.initializeIfNeeded()
            }
            PreviewAppTheme {
// ...
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
