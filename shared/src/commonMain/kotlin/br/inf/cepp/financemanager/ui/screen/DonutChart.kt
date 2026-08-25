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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.model.MonthlyExpensePerCategoryViewData
import br.inf.cepp.financemanager.ui.util.LucideIcon
import br.inf.cepp.financemanager.util.LocalPlatformUtils
import androidx.compose.ui.platform.LocalLocale
import br.inf.cepp.financemanager.model.totalExpensesWithCurrencySymbol
import br.inf.cepp.financemanager.util.today

/**
 * Formed via a low-level graphics Canvas using drawArc properties to systematically
 * slice out percentages calculated directly from standard double values.
 */
@Composable
fun DonutChart (categories: List<MonthlyExpensePerCategoryViewData>) {
    val utils = LocalPlatformUtils.current
    val totalSpent = totalExpensesWithCurrencySymbol(
        utils.getCurrentCurrencySymbol(),
        today().month
    )
    val logger = LocalDebugLog.current
    val totalPercentage = categories.sumOf { c -> c.percentage }
//    logger("Drawing DonutChart. TotalPercentage: [$totalPercentage]")
//    utils.log("Drawing DonutChart. TotalPercentage: [$totalPercentage]")
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(200.dp)
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            var startAngle = -90f
            categories.forEach { category ->
                val sweepAngle = ((category.percentage / 100f) * 360f).toFloat()
                drawArc(
                    color = category.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle - 2f, // Subtle gap spacing
                    useCenter = false,
                    style = Stroke(width = 30.dp.toPx())
                )
                startAngle += sweepAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TOTAL", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Text(totalSpent, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

/**
 * Blends row arrangements alongside a native Material 3 LinearProgressIndicator
 * to render accurate modern tracks matching the corresponding icon styles.
 */
@Composable
fun CategoryProgressRow(category: MonthlyExpensePerCategoryViewData) {
    val utils = LocalPlatformUtils.current

    // 1. Master horizontal container
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 2. Lucide Home Icon colored to match your design palette
        LucideIcon(category.icon,
            contentDescription = category.name,
            tint = category.color,
            modifier = Modifier
                .size(32.dp)
            )
        Spacer(modifier = Modifier.width(16.dp))

        // 3. Right-hand stacked structural content box
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Text Row: Labels positioned on opposite sides
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A) // Dark slate
                )
                Text(
                    text = "${utils.getCurrentCurrencySymbol()} ${String.format(LocalLocale.current.platformLocale, "%.2f", category.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            // Progress Bar Row: Indicator bar and trailing percentage label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { (category.percentage / 100f).toFloat() }, // Matches your 78% image metric
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp),
                    color = category.color, // Main filled track color
                    trackColor = Color(0xFFF1F5F9), // Light background track color
                    strokeCap = StrokeCap.Round // Smooth rounded corners
                )

                Text(
                    text = "%.2f".format(category.percentage),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF94A3B8) // Muted gray accent
                )
            }
        }
    }
}
