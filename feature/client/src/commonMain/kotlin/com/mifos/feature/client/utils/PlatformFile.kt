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

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.write

suspend fun ImageBitmap.toPlatformFile(fileName: String): PlatformFile {
    val bytearray = this.encodeToByteArray(ImageFormat.PNG)
    val outFile = FileKit.filesDir / "$fileName.png"
    outFile.write(bytearray)
    return compressImage(outFile, fileName)
}

suspend fun ByteArray.toPlatformFile(fileName: String): PlatformFile {
    val outFile = FileKit.filesDir / fileName
    outFile.write(this)
    return outFile
}
