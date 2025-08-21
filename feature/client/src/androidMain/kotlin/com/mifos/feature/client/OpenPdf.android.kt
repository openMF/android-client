package com.mifos.feature.client.utils

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.appContext
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
