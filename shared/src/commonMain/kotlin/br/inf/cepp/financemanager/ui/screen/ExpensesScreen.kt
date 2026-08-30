package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExpensesScreen(onOpenExport: () -> Unit) {
    val viewModel: ExpensesViewModel = koinViewModel()
    val expenses by viewModel.expenses.collectAsState()
    val selected = remember { mutableStateMapOf<Long, Boolean>() }
    var selectAll by remember { mutableStateOf(true) }

    LaunchedEffect(expenses) {
        expenses.forEach { expense -> selected.putIfAbsent(expense.id, true) }
        selected.keys.retainAll(expenses.map { it.id }.toSet())
        selectAll = expenses.isNotEmpty() && expenses.all { selected[it.id] != false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expenses", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF9FAFC)
    ) { padding ->
        Surface(modifier = Modifier.fillMaxSize().padding(padding), color = Color(0xFFF9FAFC)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        expenses.forEach { selected[it.id] = true }
                        selectAll = true
                    }) { Text("Select all") }
                    Button(onClick = {
                        expenses.forEach { selected[it.id] = false }
                        selectAll = false
                    }) { Text("Deselect all") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (expenses.isEmpty()) {
                    Text("No expenses recorded.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f, fill = true)) {
                        items(expenses, key = { it.id }) { e ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selected[e.id] = !(selected[e.id] ?: true) }
                            ) {
                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    IconButton(onClick = { selected[e.id] = !(selected[e.id] ?: true) }) {
                                        Icon(
                                            imageVector = if (selected[e.id] == true) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                                            contentDescription = "Toggle selection"
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(e.description, fontWeight = FontWeight.Medium)
                                            Text("R$ %.2f".format(e.amount), color = Color(0xFF4F46E5))
                                        }
                                        Text(e.date.toString(), color = Color(0xFF6B7280), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onOpenExport,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("Open Export", color = Color.White)
                }
            }
        }
    }
}
