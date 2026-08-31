package br.inf.cepp.financemanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.inf.cepp.financemanager.model.ExpenseCategory
import br.inf.cepp.financemanager.repository.allCategories
import br.inf.cepp.financemanager.ui.util.HexColor
import br.inf.cepp.financemanager.ui.util.LucideIcon
import br.inf.cepp.financemanager.ui.util.LucideIcons
import br.inf.cepp.financemanager.ui.util.lucidIconVector
import br.inf.cepp.financemanager.ui.util.toComposeColor
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.StepBack


// Pure representation of your data layer properties translated to local UI definitions
data class LocalUISelection(val key: String, val hex: String)

@Preview
@Composable
fun PreviewCategoryManager() {
    PreviewAppTheme { 
        CategoryManagerScreen(
            allCategories,
            onBackClick(),
            onSaveNewCategory()
        )
    }
}

fun onBackClick() : () -> Unit {

    return object : Function0<Unit> {
        override fun invoke() {
            println("Back!")
        }
    }
}

fun onSaveNewCategory() : (name: String, hexColor: HexColor, iconKey: String) -> Unit {
    val name1 = "Test"
    val hexColor1 = HexColor("0xFFFACC15")
    val iconKey1 = LucideIcons.ShoppingCart.name
    return object : Function3<String, HexColor, String, Unit> {
        override fun invoke(p1: String, p2: HexColor, p3: String) {
            println("Invoked with: $p1, $p2, $p3")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagerScreen(
    categories: List<ExpenseCategory>,
    onBackClick: () -> Unit,
    onSaveNewCategory: (name: String, hexColor: HexColor, iconKey: String) -> Unit,
    onUpdateCategory: (ExpenseCategory) -> Unit = {},
    onDeleteCategory: (String) -> Unit = {}
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text("Categories", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        editingCategory = null
                        showBottomSheet = true
                    }) {
                        Text("Add New", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    val uiColor = category.color.toComposeColor()
                    val uiIcon = category.iconKey.lucidIconVector()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(uiColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = uiIcon, contentDescription = category.name, tint = uiColor, modifier = Modifier.size(22.dp))
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = category.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = {
                                editingCategory = category
                                showBottomSheet = true
                            }) {
                                Text("Edit", color = MaterialTheme.colorScheme.secondary)
                            }
                            TextButton(onClick = { onDeleteCategory(category.name) }) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            var categoryName by remember(editingCategory) { mutableStateOf(editingCategory?.name ?: "") }
            val availableColors = remember { listOf("#0B1F33", "#123A5A", "#1F5A82", "#2F80C0", "#9AA7B2", "#287A5A", "#A87519", "#B54848") }
            var selectedColorKey by remember(editingCategory) { mutableStateOf(editingCategory?.color?.hex ?: availableColors.first()) }
            var selectedIconEnum by remember(editingCategory) { mutableStateOf(editingCategory?.iconKey?.let { iconKey -> LucideIcons.entries.firstOrNull { it.name == iconKey } } ?: LucideIcons.Utensils) }

            val safeSelectedColor = selectedColorKey.ifBlank { availableColors.first() }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(if (editingCategory == null) "New Category" else "Edit Category", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)

                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.secondary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Theme Color", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        availableColors.forEach { hex ->
                            val parsed = HexColor(hex).toComposeColor()
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(parsed)
                                    .border(
                                        width = if (safeSelectedColor == hex) 3.dp else 0.dp,
                                    color = if (safeSelectedColor == hex) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorKey = hex }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Select Icon", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.height(110.dp)
                    ) {
                        items(LucideIcons.entries) { item ->
                            val isSelected = selectedIconEnum == item
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedIconEnum = item },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.vector,
                                    contentDescription = item.name,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (categoryName.isNotBlank()) {
                            val color = HexColor(safeSelectedColor)
                            val iconKey = selectedIconEnum.name
                            if (editingCategory == null) {
                                onSaveNewCategory(categoryName, color, iconKey)
                            } else {
                                onUpdateCategory(editingCategory!!.copy(name = categoryName, color = color, iconKey = iconKey))
                            }
                            editingCategory = null
                            showBottomSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).padding(vertical = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(if (editingCategory == null) "Save Category" else "Update Category", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
