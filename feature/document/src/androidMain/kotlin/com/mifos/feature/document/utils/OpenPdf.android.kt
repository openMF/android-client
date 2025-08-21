package com.mifos.feature.document.utils

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

actual fun openPdf(path: String) {
    val file = File(path)
    val uri: Uri = FileProvider.getUriForFile(
        appContext,
        "${appContext.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    appContext.startActivity(Intent.createChooser(intent, "Open PDF"))
}

actual fun openImage(filePath: String) {
    // ✅ Use appContext instead of getApplicationContext()
    val file = File(filePath)
    val uri = FileProvider.getUriForFile(
        appContext,
        "${appContext.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    appContext.startActivity(intent)
}
