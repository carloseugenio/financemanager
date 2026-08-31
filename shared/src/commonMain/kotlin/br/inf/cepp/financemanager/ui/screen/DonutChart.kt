package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.model.MonthlyExpensePerCategoryViewData
import br.inf.cepp.financemanager.ui.util.LucideIcon
import br.inf.cepp.financemanager.util.LocalPlatformUtils
import br.inf.cepp.financemanager.util.today

@Composable
fun DonutChart(
    categories: List<MonthlyExpensePerCategoryViewData>,
    selectedMonth: kotlinx.datetime.Month = today().month,
    totalAmountLabel: String? = null
) {
    val colors = MaterialTheme.colorScheme
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(220.dp)
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            var startAngle = -90f
            categories.forEach { category ->
                val sweepAngle = ((category.percentage / 100f) * 360f).toFloat()
                drawArc(
                    color = category.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle - 2f,
                    useCenter = false,
                    style = Stroke(width = 30.dp.toPx(), cap = StrokeCap.Round)
                )
                startAngle += sweepAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(selectedMonth.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 10.sp, color = colors.onSurfaceVariant)
            Text(totalAmountLabel ?: "—", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text("Allocation", fontSize = 11.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
fun CategoryProgressRow(category: MonthlyExpensePerCategoryViewData) {
    val utils = LocalPlatformUtils.current
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LucideIcon(
            category.icon,
            contentDescription = category.name,
            tint = category.color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )
                Text(
                    text = "${utils.getCurrentCurrencySymbol()} ${String.format(LocalLocale.current.platformLocale, "%.2f", category.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { (category.percentage / 100f).toFloat() },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp),
                    color = category.color,
                    trackColor = colors.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                Text(
                    text = "%.2f%%".format(category.percentage),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}
