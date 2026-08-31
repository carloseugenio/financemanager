package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.ui.util.LucideIcon
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TrendingUp

@Composable
fun TopSummaryCard(
    totalSpent: String,
    transactionCount: Int,
    categoryCount: Int,
    accountCount: Int,
    monthLabel: String,
    plannedBudget: String? = null,
    budgetUtilizationPercent: Double? = null
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Monthly overview", color = colors.onPrimaryContainer.copy(alpha = 0.82f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(monthLabel, color = colors.onPrimaryContainer, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(totalSpent, color = colors.onPrimaryContainer, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    if (plannedBudget != null && budgetUtilizationPercent != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Planned budget $plannedBudget",
                            color = colors.onPrimaryContainer.copy(alpha = 0.82f),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (budgetUtilizationPercent.coerceIn(0.0, 100.0) / 100.0).toFloat() },
                            modifier = Modifier.fillMaxWidth(),
                            color = colors.secondary,
                            trackColor = colors.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${budgetUtilizationPercent.coerceAtLeast(0.0).toInt()}% of planned budget used",
                            color = colors.onPrimaryContainer.copy(alpha = 0.82f),
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .background(colors.secondary, shape = RoundedCornerShape(12.dp))
                        .size(40.dp)
                ) {
                    LucideIcon(
                        imageVector = Lucide.TrendingUp,
                        contentDescription = "Current trend",
                        tint = colors.onSecondary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricSubItem(label = "Transactions", value = "$transactionCount", modifier = Modifier.weight(1f))
                MetricSubItem(label = "Categories", value = "$categoryCount", modifier = Modifier.weight(1f))
                MetricSubItem(label = "Accounts", value = "$accountCount", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MetricSubItem(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .background(colors.surfaceVariant, RoundedCornerShape(14.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(14.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Column {
            Text(label, color = colors.onSurfaceVariant, fontSize = 10.sp)
            Text(value, color = colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BudgetVsActualCard(actualAmount: String, budgetAmount: String, utilizationPercent: Double) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Budget vs actual", fontWeight = FontWeight.SemiBold, color = colors.onSurface, fontSize = 16.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Actual", color = colors.onSurfaceVariant, fontSize = 12.sp)
                    Text(actualAmount, color = colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Planned", color = colors.onSurfaceVariant, fontSize = 12.sp)
                    Text(budgetAmount, color = colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
            LinearProgressIndicator(
                progress = { (utilizationPercent.coerceIn(0.0, 100.0) / 100.0).toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = if (utilizationPercent <= 100.0) colors.secondary else colors.error,
                trackColor = colors.surfaceVariant
            )
            Text(
                text = "${utilizationPercent.coerceAtLeast(0.0).toInt()}% of planned spending",
                color = colors.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MonthlyPerformanceCard(currentAmount: String, previousAmount: String, deltaPercent: Double) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Monthly performance", fontWeight = FontWeight.SemiBold, color = colors.onSurface, fontSize = 16.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PerformanceBar(label = "Current", value = currentAmount, fraction = 1f, accent = colors.primary, modifier = Modifier.weight(1f))
                PerformanceBar(label = "Previous", value = previousAmount, fraction = 0.7f, accent = colors.secondary, modifier = Modifier.weight(1f))
            }
            Text(
                text = if (deltaPercent >= 0.0) {
                    "Up ${deltaPercent.toInt()}% vs previous month"
                } else {
                    "Down ${kotlin.math.abs(deltaPercent).toInt()}% vs previous month"
                },
                color = colors.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun PerformanceBar(label: String, value: String, fraction: Float, accent: Color, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = colors.onSurfaceVariant, fontSize = 11.sp)
        Text(value, color = colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = { fraction.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            color = accent,
            trackColor = colors.surfaceVariant
        )
    }
}
