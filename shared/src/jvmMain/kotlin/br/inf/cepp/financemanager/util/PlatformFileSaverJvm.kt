package br.inf.cepp.financemanager.util

import java.awt.GraphicsEnvironment
import java.io.File
import javax.swing.JFileChooser

actual fun saveToFile(filename: String, content: ByteArray, destination: String?): String {
    if (!destination.isNullOrBlank()) {
        return try {
            val target = File(destination)
            target.parentFile?.mkdirs()
            target.writeBytes(content)
            target.absolutePath
        } catch (t: Throwable) {
            "error:${t.message}"
        }
    }

    val fallbackPath = {
        val tmp = System.getProperty("java.io.tmpdir") ?: "."
        val out = File(tmp, filename)
        out.parentFile?.mkdirs()
        out.writeBytes(content)
        out.absolutePath
    }

    if (GraphicsEnvironment.isHeadless()) {
        return fallbackPath()
    }

    return try {
        val chooser = JFileChooser()
        chooser.dialogTitle = "Choose export destination"
        chooser.fileSelectionMode = JFileChooser.FILES_ONLY
        chooser.selectedFile = File(filename)

        val result = chooser.showSaveDialog(null)
        if (result != JFileChooser.APPROVE_OPTION) {
            return ""
        }

        val target = chooser.selectedFile ?: return fallbackPath()
        val finalFile = if (target.extension.isEmpty()) {
            File(target.absolutePath + ".${filename.substringAfterLast('.', "export").ifBlank { "export" }}")
        } else {
            target
        }

        finalFile.parentFile?.mkdirs()
        finalFile.writeBytes(content)
        finalFile.absolutePath
    } catch (t: Throwable) {
        fallbackPath()
    }
}
