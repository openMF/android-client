package com.mifos.core.ui.util

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun ImageToByteArray(image:String?): ByteArray? {
    if (image.isNullOrBlank()) return null
    val base64String = image.substringAfter(",")
    val cleanBase64 = base64String
        .replace("\\s".toRegex(), "")
        .replace("[^A-Za-z0-9+/=]".toRegex(), "")

    try {
        val decodedBytes = Base64.decode(cleanBase64)
        val decodedBitmap = ImageUtil.compressImage(decodedBytes)
        return decodedBitmap
    } catch (e: Exception) {
        return null
    }
}