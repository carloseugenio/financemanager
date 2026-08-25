package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.model.allAccounts
import br.inf.cepp.financemanager.model.confirmedExpensesInMonth
import br.inf.cepp.financemanager.model.monthlyExpensesData
import br.inf.cepp.financemanager.model.totalExpensesWithCurrencySymbol
import br.inf.cepp.financemanager.ui.util.LucideIcon
import br.inf.cepp.financemanager.util.LocalPlatformUtils
import br.inf.cepp.financemanager.util.today
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TrendingUp

/**
 * Uses custom composable boxes arranged with a weighted Row component
 * to easily handle different dynamic screen widths.
 */
@Composable
fun TopSummaryCard() {
    // Access the current implementation of PlatformUtils interface
    val utils = LocalPlatformUtils.current
    val totalSpent = totalExpensesWithCurrencySymbol(
        utils.getCurrentCurrencySymbol(),
        today().month
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF7B2CBF)) // Purple primary
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Expenses in ${utils.formatMonthYear(today())}", color = Color(0xFFE0AAFF), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(totalSpent, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .background(Color(0xFF9D4EDD), shape = RoundedCornerShape(12.dp))
                        .size(40.dp)
                ) {
                    LucideIcon(
                        imageVector = Lucide.TrendingUp,
                        contentDescription = "Your current trending",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Horizontal Metric Elements
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricSubItem(label = "Transactions", value = "${confirmedExpensesInMonth(today().month).size}", modifier = Modifier.weight(1f))
                MetricSubItem(label = "Categories", value = "${monthlyExpensesData(today().month).size}", modifier = Modifier.weight(1f))
                MetricSubItem(label = "Accounts", value = "${allAccounts.size}", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MetricSubItem(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF9D4EDD).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Column {
            Text(label, color = Color(0xFFE0AAFF), fontSize = 10.sp)
            Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
