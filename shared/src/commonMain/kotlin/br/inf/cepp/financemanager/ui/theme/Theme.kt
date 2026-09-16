package br.inf.cepp.financemanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4A91C5),
    onPrimary = Color(0xFFE8EEF3),
    primaryContainer = Color(0xFF13283A),
    onPrimaryContainer = Color(0xFFE8EEF3),
    secondary = Color(0xFF73B1D8),
    onSecondary = Color(0xFF08131F),
    background = Color(0xFF08131F),
    onBackground = Color(0xFFE8EEF3),
    surface = Color(0xFF0D1D2C),
    onSurface = Color(0xFFE8EEF3),
    surfaceVariant = Color(0xFF13283A),
    onSurfaceVariant = Color(0xFFAAB9C5),
    outline = Color(0xFF263D50),
    error = Color(0xFFD06A6A),
    onError = Color(0xFF08131F),
    errorContainer = Color(0xFF3A1E25),
    onErrorContainer = Color(0xFFFCE8E8)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0B1F33),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF123A5A),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF1F5A82),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFEEF2F5),
    onBackground = Color(0xFF172B3A),
    surface = Color(0xFFF5F7FA),
    onSurface = Color(0xFF172B3A),
    surfaceVariant = Color(0xFFD7E0E7),
    onSurfaceVariant = Color(0xFF5B6B78),
    outline = Color(0xFFD7E0E7),
    error = Color(0xFFB54848),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFBE6E6),
    onErrorContainer = Color(0xFF5E1B1B)
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
