package br.inf.cepp.financemanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.model.ExpenseItem
import br.inf.cepp.financemanager.util.LocalPlatformUtils
import androidx.compose.ui.platform.LocalLocale
import br.inf.cepp.financemanager.ui.util.LucideIcon
import br.inf.cepp.financemanager.ui.util.lucidIconVector
import br.inf.cepp.financemanager.ui.util.toComposeColor


// 1. Individual Transaction Row Component
@Composable
fun ExpenseRow(
    item: ExpenseItem,
    modifier: Modifier = Modifier
) {
    val utils = LocalPlatformUtils.current
    val colors = MaterialTheme.colorScheme
    val formattedDate = utils.formatMonthYear(item.date) // e.g. "09/09/2026" formats cleanly
    val currencySymbol = utils.getCurrentCurrencySymbol()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dynamic Lucide/Custom Icon Wrapper
        LucideIcon(
            imageVector = item.iconKey.lucidIconVector(),
            contentDescription = item.title,
            modifier = Modifier
                .size(40.dp)
                .background(
                    item.iconColor.toComposeColor().copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp),
            tint = item.iconColor.toComposeColor()
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Center Content: Title and Date Text Labels
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.date.toString(), // Or custom day parsing: "09/09/2026"
                fontSize = 12.sp,
                color = colors.onSurfaceVariant
            )
        }

        // Right Content: Numeric Money Metrics
        Text(
            text = "$currencySymbol ${String.format(LocalLocale.current.platformLocale, "%,.2f", item.amount)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )
    }
}

// 2. Reusable Card Wrapper for both "Planned" and "Recent" Section Blocks
@Composable
fun ExpenseSectionCard(
    title: String,
    sectionIcon: ImageVector,
    iconTint: Color,
    items: List<ExpenseItem>,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.surface)
            .padding(20.dp)
    ) {
        // Section Header Row Block
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            LucideIcon(
                imageVector = sectionIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
        }

        // List Renderer with Light Inter-Row Divider Lines
        items.forEachIndexed { index, item ->
            ExpenseRow(item = item)
            if (index < items.lastIndex) {
                HorizontalDivider(color = colors.outline.copy(alpha = 0.6f), thickness = 1.dp)
            }
        }
    }
}
