package com.mifos.feature.client.utils


import java.awt.Desktop
import java.io.File

actual fun openPdf(path: String) {
    val file = File(path)
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(file)
    }
}
