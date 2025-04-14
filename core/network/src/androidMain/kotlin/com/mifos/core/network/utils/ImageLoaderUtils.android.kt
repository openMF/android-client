package com.mifos.core.network.utils

import android.content.Context
import coil3.request.ImageRequest

actual fun buildPlatformImageRequest(
    context: Any,
    imageUrl: String,
): ImageRequest {
    require(context is Context) { "Android context is required" }

    return ImageRequest.Builder(context)
        .data(imageUrl)
        .build()
}