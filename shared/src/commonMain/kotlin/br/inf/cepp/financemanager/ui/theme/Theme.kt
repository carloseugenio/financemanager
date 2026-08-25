package br.inf.cepp.financemanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    // Icon color default for dark mode (White/Off-white)
    onBackground = Color(0xFFE3E3E3),
    onSurface = Color(0xFFE3E3E3)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    // Icon color default for light mode (Black/Dark Slate)
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}