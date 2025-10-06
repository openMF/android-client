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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.mifos.core.ui.components.MifosViewPdf
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.jetbrains.skia.Image
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

@Composable
actual fun PdfPreview(pdfBytes: ByteArray, modifier: Modifier) {
    val bitmaps = remember { mutableStateListOf<ImageBitmap>() }

    LaunchedEffect(pdfBytes) {
        bitmaps.clear()
        PDDocument.load(pdfBytes).use { doc ->
            val renderer = PDFRenderer(doc)
            for (i in 0 until doc.numberOfPages) {
                val awtImg: BufferedImage = renderer.renderImageWithDPI(i, 150f)
                val byteOut = java.io.ByteArrayOutputStream()
                ImageIO.write(awtImg, "png", byteOut)
                val skiaImage = Image.makeFromEncoded(byteOut.toByteArray())
                bitmaps.add(skiaImage.toComposeImageBitmap())
            }
        }
    }

    MifosViewPdf(bitmaps, modifier = modifier)
}
