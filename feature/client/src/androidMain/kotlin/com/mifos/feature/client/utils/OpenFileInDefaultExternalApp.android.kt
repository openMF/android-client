package com.mifos.feature.client.utils

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.core.content.FileProvider
import com.mifos.core.ui.util.getMimeTypeFromPlatformFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.context
import io.github.vinceglb.filekit.path
import java.io.File

actual fun openFileWithDefaultExternalApp(platformFile: PlatformFile) {

    val context = FileKit.context

    val file = File(platformFile.path)
    val mimeType = getMimeTypeFromPlatformFile(platformFile)
    val intent = Intent(ACTION_VIEW)


    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file,
    )

    intent.setDataAndType(uri, mimeType)

    context.startActivity(intent)

}