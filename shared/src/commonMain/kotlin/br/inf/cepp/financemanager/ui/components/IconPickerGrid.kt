package br.inf.cepp.financemanager.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IconPickerGrid(
    onIconSelected: (String) -> Unit
) {
//    val iconsPool = LucideIconRegistry.getAvailableIcons()
//
//    LazyVerticalGrid(
//        columns = GridCells.Adaptive(56.dp)
//    ) {
//        items(iconsPool) { (key, imageVector) ->
//            Icon(
//                imageVector = imageVector,
//                contentDescription = key,
//                modifier = Modifier
//                    .clickable { onIconSelected(key) } // Saves the plain string (e.g. "Car")
//            )
//        }
//    }
}
