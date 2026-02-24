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

suspend fun compressImage(imageFile: PlatformFile, clientId: String): PlatformFile {
    // TODO: Image compression and direct file access via FileKit are
    //  currently disabled because they are not supported across all KMP targets.

// Implement platform-specific handling before enabling this.
//    val bytes = FileKit.compressImage(
//        file = imageFile,
//        imageFormat = ImageFormat.PNG,
//        quality = 100,
//        maxHeight = 150,
//    )
//    val outFile = FileKit.filesDir / "client_image_$clientId.png"
//    outFile.write(bytes)
    return imageFile
}
