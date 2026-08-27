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
    onSaveNewCategory: (name: String, hexColor: HexColor, iconKey: String) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Base Application Canvas (Light Gray/White Background)
    Scaffold(
        containerColor = Color(0xFFF9FAFC),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text("Categories", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                        LucideIcon(
//                            imageVector = Lucide.StepBack,
//                            contentDescription = "Back",
//                            tint = MaterialTheme.colorScheme.onPrimary
//                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showBottomSheet = true }) {
                        Text("Add New", color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            )
        }
    ) { paddingValues ->
        // Main structural categories card array
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
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
                        .background(Color.White)
                        .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Soft background wrapper matching item style guide
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
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }
    }

    // Modal creation surface mapping properties safely back to core models
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            var categoryName by remember { mutableStateOf("") }

            // Available picker options matching your structural components
            val availableColors = remember { listOf("#F59E0B", "#EF4444", "#3B82F6", "#10B981", "#8B5CF6") }
            var selectedColorKey by remember { mutableStateOf(availableColors.first()) }
            var selectedIconEnum by remember { mutableStateOf(LucideIcons.Utensils) }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("New Category", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))

                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7C3AED),
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    )
                )

                // Palette Section Block
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Theme Color", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFF64748B))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        availableColors.forEach { hex ->
                            val parsed = HexColor(hex).toComposeColor()
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(parsed)
                                    .border(
                                        width = if (selectedColorKey == hex) 3.dp else 0.dp,
                                        color = if (selectedColorKey == hex) Color(0xFF1E293B) else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorKey = hex }
                            )
                        }
                    }
                }

                // Grid Registry Selector Block
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select Icon", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFF64748B))
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
                                    .background(if (isSelected) Color(0xFF7C3AED).copy(alpha = 0.1f) else Color(0xFFF1F5F9))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.dp,
                                        color = if (isSelected) Color(0xFF7C3AED) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedIconEnum = item },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.vector,
                                    contentDescription = item.name,
                                    tint = if (isSelected) Color(0xFF7C3AED) else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (categoryName.isNotBlank()) {
                            onSaveNewCategory(categoryName, HexColor(selectedColorKey), selectedIconEnum.name)
                            showBottomSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp).padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Text("Save Category", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
