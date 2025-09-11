package com.mifos.feature.client.utils

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentInteractionController

@OptIn(ExperimentalForeignApi::class)
actual fun openFileWithDefaultExternalApp(platformFile: PlatformFile) {

    val opened = UIApplication.sharedApplication.openURL(platformFile.nsUrl)

    if (!opened) {
        val documentController = UIDocumentInteractionController()
        documentController.URL = platformFile.nsUrl

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController

        if (rootViewController != null) {
            // Present the options menu to let user choose how to open
            documentController.presentOptionsMenuFromRect(
                rect = rootViewController.view.bounds,
                inView = rootViewController.view,
                animated = true
            )
        }
    }

}