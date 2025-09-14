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

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap
import com.mifos.core.ui.components.MifosViewPdf
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun PdfPreview(pdfBytes: ByteArray, modifier: Modifier) {
    val context = LocalContext.current
    val bitmaps = remember { mutableStateListOf<Bitmap>() }

    LaunchedEffect(pdfBytes) {
        bitmaps.clear()

        val tempFile = File.createTempFile("temp", ".pdf", context.cacheDir)
        FileOutputStream(tempFile).use { it.write(pdfBytes) }

        val renderer = PdfRenderer(
            ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY),
        )

        for (i in 0 until renderer.pageCount) {
            val page = renderer.openPage(i)

            val scale = 3f
            val bmp = createBitmap((page.width * scale).toInt(), (page.height * scale).toInt())

            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()

            bitmaps.add(bmp)
        }
        renderer.close()
    }

    MifosViewPdf(bitmaps)
}
