package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.model.monthlyExpensesData
import br.inf.cepp.financemanager.util.today

val LocalDebugLog = staticCompositionLocalOf { { _: String -> } }

@Preview
@Composable
fun OpenPreview() {
    val logs = remember { mutableStateListOf<String>() }

    CompositionLocalProvider(LocalDebugLog provides { message -> logs.add(message) }) {
        Box {
            Row {
                Column {
                    PreviewAppTheme {
                        FinanceDashboardScreen(onBackClick())
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                // Console overlay
                LazyColumn(
                    modifier = Modifier
                        .border(1.dp, color = Color.Black)
                        //.align(Alignment.BottomCenter)
                ) {
                    items(logs) {
                        Text(
                            "LogItem: $it",
                            color = Color.Black,
                            fontSize = 20.sp,
                        )
                    }
                }
            }
        }
    }
}
