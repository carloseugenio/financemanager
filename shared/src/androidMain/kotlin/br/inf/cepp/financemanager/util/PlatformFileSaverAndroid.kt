package br.inf.cepp.financemanager.util

import java.io.File
import java.io.FileOutputStream
import br.inf.cepp.financemanager.AndroidContext

actual fun saveToFile(filename: String, content: ByteArray): String {
    try {
        val ctx = AndroidContext.appContext
        val dir = ctx.getExternalFilesDir(null) ?: ctx.filesDir
        val outFile = File(dir, filename)
        FileOutputStream(outFile).use { it.write(content) }
        return outFile.absolutePath
    } catch (t: Throwable) {
        return "error:${t.message}"
    }
}
