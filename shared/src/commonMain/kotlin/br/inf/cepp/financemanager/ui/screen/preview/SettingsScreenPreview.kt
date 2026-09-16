package br.inf.cepp.financemanager.ui.screen.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.inf.cepp.financemanager.ui.screen.PreviewAppTheme
import br.inf.cepp.financemanager.ui.screen.SettingsScreenContent
import br.inf.cepp.financemanager.util.DateEntryMode

@Preview
@Composable
fun SettingsScreenPreview() {
    PreviewAppTheme {
        SettingsScreenContent(
            dateEntryMode = DateEntryMode.FREE_HAND,
            onDateEntryModeChange = { },
            onBack = { }
        )
    }
}
