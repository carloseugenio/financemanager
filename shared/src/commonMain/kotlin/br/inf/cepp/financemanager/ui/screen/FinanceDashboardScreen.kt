package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.model.monthlyExpensesData
import br.inf.cepp.financemanager.model.plannedExpenses
import br.inf.cepp.financemanager.model.recentExpenses
import br.inf.cepp.financemanager.ui.components.ExpenseSectionCard
import br.inf.cepp.financemanager.util.today
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TrendingDown

/**
 * Main screen Layout
 * Serves as the main scroll container utilizing a lazy structure.
 * This handles performance optimizations when dealing with large transaction sets.
 */
@Composable
fun FinanceDashboardScreen() {
    val viewData = monthlyExpensesData(today().month)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF7F9FC) // Soft gray background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Purple Summary Card
            item { TopSummaryCard() }

            // Main Breakdown Container Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Expenses by Category",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF222222),
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Donut Chart
                        DonutChart(categories = viewData)

                        Spacer(modifier = Modifier.height(32.dp))

                        // List of categories and bars
                        viewData.forEach { category ->
                            CategoryProgressRow(category = category)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

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

