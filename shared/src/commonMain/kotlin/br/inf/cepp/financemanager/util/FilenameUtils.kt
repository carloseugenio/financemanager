package br.inf.cepp.financemanager.util

fun sanitizeFilename(name: String, fallback: String = "export"): String {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return fallback
    // keep letters, digits, dot, dash, underscore
    val cleaned = trimmed.map {
        if (it.isLetterOrDigit() || it == '.' || it == '-' || it == '_') it else '_'
    }.joinToString("")
    // collapse multiple underscores
    val collapsed = cleaned.replace(Regex("_+"), "_")
    return if (collapsed.length > 200) collapsed.take(200) else collapsed
}
