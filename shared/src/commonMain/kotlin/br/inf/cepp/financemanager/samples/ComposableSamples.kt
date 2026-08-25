package br.inf.cepp.financemanager.samples

import androidx.compose.runtime.Composable
import br.inf.cepp.financemanager.util.LocalPlatformUtils

class ComposableSamples {

    @Suppress("ComposableNaming")
    @Composable
    fun useLocalPlatformUtils() {
        val utils = LocalPlatformUtils.current
        println("Currency symbol: ${utils.getCurrentCurrencySymbol()}")
    }
}