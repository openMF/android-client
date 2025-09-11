/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.utils

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentInteractionController

@OptIn(ExperimentalForeignApi::class)
actual suspend fun openPdfWithDefaultExternalApp(platformFile: PlatformFile) {
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
                animated = true,
            )
        }
    }
}
