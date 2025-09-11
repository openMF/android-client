package com.mifos.feature.client.utils

import io.github.vinceglb.filekit.PlatformFile
import java.awt.Desktop

actual suspend fun openPdfWithDefaultExternalApp(platformFile: PlatformFile) {
    val pdfFile = platformFile.file
    Desktop.getDesktop().open(pdfFile)
}