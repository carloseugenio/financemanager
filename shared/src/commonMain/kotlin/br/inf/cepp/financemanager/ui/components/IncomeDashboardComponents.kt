package br.inf.cepp.financemanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.model.Income
import br.inf.cepp.financemanager.ui.util.toComposeColor
import br.inf.cepp.financemanager.util.LocalPlatformUtils
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TrendingUp

@Composable
fun IncomeRow(
    item: Income,
    modifier: Modifier = Modifier
) {
    val utils = LocalPlatformUtils.current
    val colors = MaterialTheme.colorScheme
    val currencySymbol = utils.getCurrentCurrencySymbol()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Lucide.TrendingUp,
            contentDescription = item.description,
            tint = item.categoryColor().toComposeColor(),
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.categoryColor().toComposeColor().copy(alpha = 0.1f))
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${item.category.displayName()} · ${item.date}",
                fontSize = 12.sp,
                color = colors.onSurfaceVariant
            )
        }

        Text(
            text = "$currencySymbol ${String.format("%.2f", item.amount)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun IncomeSectionCard(
    title: String,
    sectionIcon: ImageVector,
    iconTint: Color,
    items: List<Income>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = sectionIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (items.isEmpty()) {
                Text(
                    text = "No planned incomes",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            } else {
                items.forEachIndexed { index, item ->
                    IncomeRow(item = item)
                    if (index < items.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), thickness = 1.dp)
                    }
                }
            }
        }
    }
}

private fun br.inf.cepp.financemanager.model.IncomeCategory.displayName(): String = name.lowercase().replaceFirstChar { it.uppercase() }

private fun br.inf.cepp.financemanager.model.Income.categoryColor(): br.inf.cepp.financemanager.ui.util.HexColor {
    return when (category) {
        br.inf.cepp.financemanager.model.IncomeCategory.SALARY -> br.inf.cepp.financemanager.ui.util.HexColor("#287A5A")
        br.inf.cepp.financemanager.model.IncomeCategory.RENTAL -> br.inf.cepp.financemanager.ui.util.HexColor("#1F5A82")
        br.inf.cepp.financemanager.model.IncomeCategory.DIVIDEND -> br.inf.cepp.financemanager.ui.util.HexColor("#2F80C0")
        else -> br.inf.cepp.financemanager.ui.util.HexColor("#123A5A")
    }
}
