package br.inf.cepp.financemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.inf.cepp.financemanager.util.AndroidPlatformUtils
import br.inf.cepp.financemanager.util.PlatformProvider

val app = FinanceManager()
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize the interface implementation for the shared code
        PlatformProvider.instance = AndroidPlatformUtils(applicationContext)

        setContent {
            app.Start()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    app.Start()
}