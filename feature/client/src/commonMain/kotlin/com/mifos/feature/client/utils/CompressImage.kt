package com.mifos.feature.client.utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.write

suspend fun compressImage(imageFile: PlatformFile, clientId:Int):PlatformFile{
    val bytes = FileKit.compressImage(
        file = imageFile,
        imageFormat = ImageFormat.PNG,
        quality = 100,
        maxHeight=150
    )
    val outFile = FileKit.filesDir / "client_image_$clientId.png"
    outFile.write(bytes)
    return outFile
}