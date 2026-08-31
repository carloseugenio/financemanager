package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.domain.ImportHistoryItem
import br.inf.cepp.financemanager.domain.ImportReviewItem
import br.inf.cepp.financemanager.domain.ImportReviewSummary
import com.composables.icons.lucide.CheckCheck
import com.composables.icons.lucide.FileUp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RefreshCw
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    onBack: () -> Unit,
    onReviewDrafts: () -> Unit = {},
) {
    val viewModel: ImportViewModel = koinViewModel()
    val reviewSummary by viewModel.reviewSummary.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Import & Reconcile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Lucide.RefreshCw, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Refresh preview")
                        }
                        Button(
                            onClick = onReviewDrafts,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(Lucide.CheckCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Review drafts")
                        }
                    }
                }

                item {
                    if (reviewSummary == null) {
                        ImportLoadingCard()
                    } else {
                        ImportReviewSummaryCard(reviewSummary!!)
                    }
                }

                if (reviewSummary != null && reviewSummary!!.duplicateCount > 0) {
                    item {
                        ImportDuplicateBanner(reviewSummary!!)
                    }
                }

                if (reviewSummary == null || reviewSummary!!.previewItems.isEmpty()) {
                    item {
                        EmptyImportState()
                    }
                } else {
                    items(reviewSummary!!.previewItems, key = { it.expense.id }) { item ->
                        ImportReviewItemCard(item)
                    }
                }

                if (reviewSummary != null && reviewSummary!!.historyItems.isNotEmpty()) {
                    item {
                        ImportHistoryCard(reviewSummary!!.historyItems)
                    }
                }
            }
        }
    }
}

@Composable
fun ImportReviewSummaryCard(summary: ImportReviewSummary) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Import preview", color = colors.onPrimaryContainer.copy(alpha = 0.8f), fontSize = 12.sp)
            Text("${summary.totalDrafts} draft item(s)", color = colors.onPrimaryContainer, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryChip("Duplicates", summary.duplicateCount.toString())
                SummaryChip("Suggestions", summary.suggestedCategoryCount.toString())
                SummaryChip("Merged names", summary.normalizedMerchantCount.toString())
            }
        }
    }
}

@Composable
fun ImportReviewItemCard(item: ImportReviewItem) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.expense.description, fontWeight = FontWeight.SemiBold, color = colors.onSurface, fontSize = 16.sp)
                    Text(item.normalizedMerchant.ifBlank { "Unnormalized" }, color = colors.onSurfaceVariant, fontSize = 12.sp)
                }
                ReviewBadge(item)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Amount", color = colors.onSurfaceVariant, fontSize = 11.sp)
                    Text(formatMoney(item.expense.amount), color = colors.onSurface, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Source", color = colors.onSurfaceVariant, fontSize = 11.sp)
                    Text(item.expense.source.name.lowercase().replaceFirstChar { it.uppercase() }, color = colors.onSurface, fontWeight = FontWeight.Bold)
                }
            }

            if (item.suggestedCategory != null) {
                Text("Suggested category: ${item.suggestedCategory.name}", color = colors.onSurfaceVariant, fontSize = 12.sp)
            }

            if (item.duplicateMatch != null) {
                Text(
                    text = "Matches ${item.duplicateMatch.description} from ${item.duplicateMatch.date}",
                    color = colors.error,
                    fontSize = 12.sp
                )
            }

            LinearProgressIndicator(
                progress = { (item.confidencePercent.coerceIn(0.0, 100.0) / 100.0).toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    item.duplicateMatch != null -> colors.error
                    item.confidencePercent >= 80.0 -> colors.primary
                    else -> colors.secondary
                },
                trackColor = colors.surfaceVariant
            )
            Text("Confidence ${item.confidencePercent.toInt()}%", color = colors.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}

@Composable
fun ImportDuplicateBanner(summary: ImportReviewSummary) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.errorContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Potential duplicates found", color = colors.onErrorContainer, fontWeight = FontWeight.SemiBold)
                Text(
                    "${summary.duplicateCount} draft item(s) match existing confirmed expenses.",
                    color = colors.onErrorContainer,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ImportHistoryCard(items: List<ImportHistoryItem>) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Recent imported items", fontWeight = FontWeight.SemiBold, color = colors.onSurface, fontSize = 16.sp)
            items.forEach { item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.expense.description, color = colors.onSurface, fontSize = 12.sp)
                        Text(item.normalizedMerchant, color = colors.onSurfaceVariant, fontSize = 11.sp)
                    }
                    Text(formatMoney(item.expense.amount), color = colors.onSurface, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun ImportLoadingCard() {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Lucide.FileUp, contentDescription = null, tint = colors.primary)
            Text("Building import preview...", color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmptyImportState() {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("No draft imports yet", fontWeight = FontWeight.SemiBold, color = colors.onSurface)
            Text("Imported statements and receipts will appear here before confirmation.", color = colors.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ReviewBadge(item: ImportReviewItem) {
    val colors = MaterialTheme.colorScheme
    val background = when {
        item.duplicateMatch != null -> colors.errorContainer
        item.confidencePercent >= 80.0 -> colors.primaryContainer
        else -> colors.surfaceVariant
    }
    val foreground = when {
        item.duplicateMatch != null -> colors.onErrorContainer
        item.confidencePercent >= 80.0 -> colors.onPrimaryContainer
        else -> colors.onSurface
    }
    val label = when {
        item.duplicateMatch != null -> "Duplicate"
        item.confidencePercent >= 80.0 -> "Ready"
        else -> "Review"
    }

    Text(
        text = label,
        color = foreground,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

@Composable
private fun SummaryChip(label: String, value: String) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .background(colors.surface.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(label, color = colors.onPrimaryContainer.copy(alpha = 0.78f), fontSize = 10.sp)
        Text(value, color = colors.onPrimaryContainer, fontWeight = FontWeight.Bold)
    }
}

private fun formatMoney(amount: Double): String {
    return "R$ ${"%.2f".format(amount)}"
}
