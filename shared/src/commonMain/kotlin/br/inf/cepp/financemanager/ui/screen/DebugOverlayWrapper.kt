package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DebugOverlayWrapper {

    @Composable
    fun DisplayWrapper(content: @Composable () -> Unit) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Your actual view remains untouched
            content()

            // Floating debug container
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(8.dp)
            ) {
                //logs.forEach { log ->
                    Text(text = "Text logging test", color = Color.Green, fontSize = 12.sp)
                //}
            }
        }
    }
}