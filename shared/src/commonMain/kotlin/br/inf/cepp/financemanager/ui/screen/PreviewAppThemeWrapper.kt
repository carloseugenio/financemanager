package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import br.inf.cepp.financemanager.ui.theme.AppTheme
import br.inf.cepp.financemanager.util.FakePlatformUtils
import br.inf.cepp.financemanager.util.LocalPlatformUtils


/**
 * A [Composable] able to provide these [CompositionLocalProvider]s:
 *
 * [LocalPlatformUtils] - used for utilities functions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewAppTheme(content: @Composable () -> Unit) {
    // Automatically uses the predictable Fake implementation for previews
    val fakeUtils = FakePlatformUtils(
        mockCurrencySymbol = "R$",
        mockFormattedDate = "Agosto 2026"
    )

    CompositionLocalProvider(LocalPlatformUtils provides fakeUtils) {
        AppTheme {
            content()
        }
    }
}
