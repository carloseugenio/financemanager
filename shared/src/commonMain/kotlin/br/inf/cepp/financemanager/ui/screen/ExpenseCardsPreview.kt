package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.inf.cepp.financemanager.model.plannedExpenses
import br.inf.cepp.financemanager.model.recentExpenses
import br.inf.cepp.financemanager.ui.components.ExpenseSectionCard
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TrendingDown

@Composable
@Preview
fun ExpenseCardsPreview() {

    PreviewAppTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Screen Section Card Block A: Planned Expenses
                ExpenseSectionCard(
                    title = "Upcoming planned expenses",
                    sectionIcon = Lucide.Calendar,
                    iconTint = Color(0xFF6366F1), // Purple Indigo tone
                    items = plannedExpenses
                )
            }

            item {
                // Screen Section Card Block B: Recent Expenses
                ExpenseSectionCard(
                    title = "Recent expenses",
                    sectionIcon = Lucide.TrendingDown,
                    iconTint = Color(0xFFEF4444), // Expense warning red tone
                    items = recentExpenses
                )
            }
        }
    }
}
