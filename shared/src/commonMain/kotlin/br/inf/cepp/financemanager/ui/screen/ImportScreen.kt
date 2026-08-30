package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    onBack: () -> Unit,
    onReviewDrafts: () -> Unit = {},
    onSelectFile: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color(0xFFF9FAFC),
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
            color = Color(0xFFF9FAFC)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onSelectFile,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Select file")
                    }
                    Button(
                        onClick = onReviewDrafts,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                    ) {
                        Text("Review drafts")
                    }
                }

                ImportSourceCard(
                    title = "Upload OFX / CSV",
                    subtitle = "Connect your bank or card statement and import transactions.",
                    status = "Ready"
                )
                ImportSourceCard(
                    title = "SMS reconciliation",
                    subtitle = "Match incoming transactions from SMS messages to a draft expense.",
                    status = "Draft"
                )
                ImportSourceCard(
                    title = "Receipt scan",
                    subtitle = "Upload a PDF or scan a receipt to extract the amount and merchant.",
                    status = "New"
                )

                DraftExpensesSection()
            }
        }
    }
}

@Composable
private fun DraftExpensesSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Draft items",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            val draftItems = listOf(
                DraftItem("Mercado Bairro", "R$ 120.00", "Bank import"),
                DraftItem("Uber", "R$ 38.40", "SMS"),
                DraftItem("Supermercado", "R$ 86.75", "Receipt")
            )

            draftItems.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(item.title, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
                        Text(item.source, fontSize = 12.sp, color = Color(0xFF6B7280))
                    }
                    Text(item.amount, fontWeight = FontWeight.SemiBold, color = Color(0xFF4F46E5))
                }
            }
        }
    }
}

private data class DraftItem(
    val title: String,
    val amount: String,
    val source: String
)

@Composable
private fun ImportSourceCard(
    title: String,
    subtitle: String,
    status: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.material3.Icon(
                    imageVector = when (title) {
                        "Upload OFX / CSV" -> Lucide.Settings
                        "SMS reconciliation" -> Lucide.Settings
                        else -> Lucide.Settings
                    },
                    contentDescription = title,
                    tint = Color(0xFF4F46E5)
                )
                androidx.compose.material3.Text(
                    text = status,
                    color = Color(0xFF0F766E),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}
