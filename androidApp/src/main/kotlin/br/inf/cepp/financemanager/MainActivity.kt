package br.inf.cepp.financemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.inf.cepp.financemanager.AndroidContext
import br.inf.cepp.financemanager.FinanceManager
import br.inf.cepp.financemanager.scheduler.AndroidRecurringExpenseScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidContext.appContext = applicationContext
        AndroidRecurringExpenseScheduler(applicationContext).start()
        setContent {
            FinanceManager().Start() // Koin boots up natively here!
        }
    }
}
