package br.inf.cepp.financemanager.util

import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import br.inf.cepp.financemanager.AndroidContext

actual fun saveToFile(filename: String, content: ByteArray, destination: String?): String {
    try {
        val ctx = AndroidContext.appContext
        if (destination.isNullOrBlank()) {
            return "error: destination required"
        }

        return if (destination.startsWith("content://")) {
            val uri = Uri.parse(destination)
            ctx.contentResolver.openOutputStream(uri, "w")?.use { it.write(content) }
            destination
        } else {
            val outFile = File(destination)
            outFile.parentFile?.mkdirs()
            FileOutputStream(outFile).use { it.write(content) }
            outFile.absolutePath
        }
    } catch (t: Throwable) {
        return "error:${t.message}"
    }
}
