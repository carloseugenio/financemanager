package br.inf.cepp.financemanager.util

import java.io.File

actual fun saveToFile(filename: String, content: ByteArray): String {
    val tmp = System.getProperty("java.io.tmpdir") ?: "."
    val out = File(tmp, filename)
    out.parentFile?.mkdirs()
    out.writeBytes(content)
    return out.absolutePath
}
