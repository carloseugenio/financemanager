package br.inf.cepp.financemanager.util

import kotlinx.datetime.LocalDate

object DateInputUtils {
    // Mask input for simple locale-aware formatting while typing
    // language: "pt" -> dd/MM/yyyy, otherwise -> yyyy-MM-dd
    fun maskForLocale(raw: String, language: String): String {
        val digits = raw.filter { it.isDigit() }
        return if (language == "pt") formatDigits(digits, intArrayOf(2, 2, 4), "/") else formatDigits(digits, intArrayOf(4, 2, 2), "-")
    }

    // Try to parse a LocalDate from a user-entered string, accepting both dd/MM/yyyy and yyyy-MM-dd
    fun parseLenient(raw: String, language: String): LocalDate? {
        val s = raw.trim()
        try {
            return when {
                s.contains('/') -> {
                    // assume dd/MM/yyyy
                    val parts = s.split('/').map { it.trim() }
                    if (parts.size >= 3) {
                        val d = parts[0].toIntOrNull() ?: return null
                        val m = parts[1].toIntOrNull() ?: return null
                        val y = parts[2].toIntOrNull() ?: return null
                        LocalDate(y, m, d)
                    } else null
                }
                s.contains('-') -> {
                    // assume yyyy-MM-dd
                    val parts = s.split('-').map { it.trim() }
                    if (parts.size >= 3) {
                        val y = parts[0].toIntOrNull() ?: return null
                        val m = parts[1].toIntOrNull() ?: return null
                        val d = parts[2].toIntOrNull() ?: return null
                        LocalDate(y, m, d)
                    } else null
                }
                else -> {
                    // try both heuristics: yyyyMMdd or ddMMyyyy
                    val digits = s.filter { it.isDigit() }
                    if (digits.length == 8) {
                        val y = digits.substring(0, 4).toIntOrNull()
                        val m = digits.substring(4, 6).toIntOrNull()
                        val d = digits.substring(6, 8).toIntOrNull()
                        if (y != null && m != null && d != null) return LocalDate(y, m, d)
                        val d2 = digits.substring(0, 2).toIntOrNull()
                        val m2 = digits.substring(2, 4).toIntOrNull()
                        val y2 = digits.substring(4, 8).toIntOrNull()
                        if (y2 != null && m2 != null && d2 != null) return LocalDate(y2, m2, d2)
                    }
                    null
                }
            }
        } catch (t: Throwable) {
            return null
        }
    }

    private fun formatDigits(digits: String, groups: IntArray, separator: String): String {
        val parts = mutableListOf<String>()
        var index = 0
        for (group in groups) {
            if (index >= digits.length) break
            val end = (index + group).coerceAtMost(digits.length)
            parts += digits.substring(index, end)
            index = end
        }
        return buildString {
            parts.forEachIndexed { i, part ->
                if (i > 0) append(separator)
                append(part)
            }
        }
    }

    fun formatForLocale(date: LocalDate, language: String): String {
        return if (language == "pt") {
            "%02d/%02d/%04d".format(date.day, date.monthNumber, date.year)
        } else {
            "%04d-%02d-%02d".format(date.year, date.monthNumber, date.day)
        }
    }
}
