package br.inf.cepp.financemanager.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import br.inf.cepp.financemanager.AndroidContext
import java.io.File

actual fun shareFile(path: String, mimeType: String): String {
    return try {
        val ctx = AndroidContext.appContext
        val file = File(path)
        if (!file.exists()) return "error: file not found"
        val authority = ctx.packageName + ".fileprovider"
        val uri: Uri = FileProvider.getUriForFile(ctx, authority, file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(Intent.createChooser(intent, "Share file"))
        "shared"
    } catch (t: Throwable) {
        "error:${t.message}"
    }
}
