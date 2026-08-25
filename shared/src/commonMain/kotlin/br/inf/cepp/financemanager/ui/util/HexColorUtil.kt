package br.inf.cepp.financemanager.ui.util

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class HexColor(val hex: String) {
    init {
        // Strip common hex prefixes to evaluate just the raw alpha/color alphanumeric chars
        val cleaned = hex.trim()
            .removePrefix("0x")
            .removePrefix("0X")
            .removePrefix("#")

        require(cleaned.matches(Regex("^[0-9a-fA-F]{6,8}$"))) {
            "Invalid Hex Format: '$hex'. Must be 6 or 8 hex characters, optionally prefixed with # or 0x."
        }
    }
}

fun HexColor.toComposeColor(): Color {
    // Standardize the string by dropping 0x or #
    val cleaned = this.hex.trim()
        .removePrefix("0x")
        .removePrefix("0X")
        .removePrefix("#")

    return when (cleaned.length) {
        // Handle RRGGBB (Inject fully opaque Alpha channel)
        6 -> {
            val argbLong = ("FF" + cleaned).toLong(16)
            Color(argbLong)
        }
        // Handle AARRGGBB
        8 -> {
            val argbLong = cleaned.toLong(16)
            Color(argbLong)
        }
        else -> throw IllegalArgumentException("Unexpected hex length after cleaning: ${this.hex}")
    }
}
