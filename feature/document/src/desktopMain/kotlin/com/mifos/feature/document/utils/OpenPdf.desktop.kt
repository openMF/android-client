package com.mifos.feature.document.utils


import java.awt.Desktop
import java.io.File

actual fun openPdf(path: String) {
    val file = File(path)
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(file)
    }
}

actual fun openImage(filePath: String) {
}