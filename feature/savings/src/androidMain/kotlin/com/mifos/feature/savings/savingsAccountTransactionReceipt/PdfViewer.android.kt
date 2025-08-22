/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountTransactionReceipt

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

/**
 * Created by Arin Yadav on 20/08/2025
 */
@Composable
actual fun PdfViewer(pdfBytes: ByteArray) {
    val context = LocalContext.current

    val pdfDir = File(context.cacheDir, "pdfs")
    if (!pdfDir.exists()) {
        pdfDir.mkdirs()
    }

    val pdfFile = File(context.cacheDir, "pdfs/receipt.pdf")
    pdfFile.writeBytes(pdfBytes)

    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        pdfFile,
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    context.startActivity(Intent.createChooser(intent, "Open PDF"))
}
