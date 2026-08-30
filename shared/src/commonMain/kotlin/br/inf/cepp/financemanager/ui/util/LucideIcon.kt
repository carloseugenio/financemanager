package br.inf.cepp.financemanager.ui.util

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import br.inf.cepp.financemanager.model.ExpenseCategory
import com.composables.icons.lucide.Activity
import com.composables.icons.lucide.Car
import com.composables.icons.lucide.DollarSign
import com.composables.icons.lucide.FileQuestionMark
import com.composables.icons.lucide.Gamepad
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShoppingBag
import com.composables.icons.lucide.ShoppingCart
import com.composables.icons.lucide.StepBack
import com.composables.icons.lucide.Tv
import com.composables.icons.lucide.Utensils
import com.composables.icons.lucide.Wrench

@Composable
fun LucideIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    // Defaults to whatever M3 token is active for
    // text/foregrounds in the current context
    tint: Color = LocalContentColor.current
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}

enum class LucideIcons(val vector: ImageVector) {
    Utensils(Lucide.Utensils),
    ShoppingCart(Lucide.ShoppingCart),
    Heart(Lucide.Heart),
    ShoppingBag(Lucide.ShoppingBag),
    Tv(Lucide.Tv),

    Wrench(Lucide.Wrench),

    Car(Lucide.Car),

    House(Lucide.House),

    Activity(Lucide.Activity),

    Gamepad(Lucide.Gamepad),

    DollarSign(Lucide.DollarSign),

    StepBack(Lucide.StepBack),

    FileQuestionMark(Lucide.FileQuestionMark),

    ;

    companion object {
        fun fromKey(key: String): LucideIcons {
            val normalized = key.trim()
                .replace('-', '_')
                .replace(' ', '_')
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

            return entries.firstOrNull { it.name == normalized }
                ?: when (key.trim()) {
                    "shopping-cart" -> ShoppingCart
                    "shopping-bag" -> ShoppingBag
                    "gamepad-2" -> Gamepad
                    "dollar-sign" -> DollarSign
                    "file-question-mark" -> FileQuestionMark
                    else -> FileQuestionMark
                }
        }

    }
}

fun String.lucidIconVector() : ImageVector = LucideIcons.fromKey(this).vector

// 2. Computed UI Extensions
val ExpenseCategory.icon: LucideIcons
    get() = LucideIcons.fromKey(this.iconKey)
