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

import androidx.compose.runtime.Composable
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * Created by Arin Yadav on 20/08/2025
 */
@Composable
actual fun PdfViewer(pdfBytes: ByteArray) {

    val tmpDir = NSTemporaryDirectory()
    val pdfPath = "$tmpDir/receipt.pdf".toPath()

    FileSystem.SYSTEM.write(pdfPath) {
        write(pdfBytes)
    }

    val url = NSURL.fileURLWithPath(pdfPath.toString())
    UIApplication.sharedApplication.openURL(url)

}