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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import io.github.vinceglb.filekit.utils.toNSData
import platform.PDFKit.PDFDocument
import platform.PDFKit.PDFView

@Composable
actual fun PdfPreview(pdfBytes: ByteArray, modifier: Modifier) {
    val data = pdfBytes.toNSData()
    val pdfDocument = PDFDocument(data)
    val pdfView = PDFView().apply {
        document = pdfDocument
        autoScales = true
    }

    UIKitView(factory = { pdfView }, modifier = modifier)
}
