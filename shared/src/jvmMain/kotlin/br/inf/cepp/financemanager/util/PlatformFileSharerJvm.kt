package br.inf.cepp.financemanager.util

actual fun shareFile(path: String, mimeType: String): String {
    return try {
        val f = java.io.File(path)
        if (!f.exists()) return "error: file not found"
        java.awt.Desktop.getDesktop().open(f)
        "opened"
    } catch (t: Throwable) {
        "error:${t.message}"
    }
}
