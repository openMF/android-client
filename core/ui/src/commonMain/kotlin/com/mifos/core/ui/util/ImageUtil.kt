package com.mifos.core.ui.util

expect object ImageUtil {
    val DEFAULT_MAX_WIDTH: Float
    val DEFAULT_MAX_HEIGHT: Float

    fun compressImage(
        decodedBytes: ByteArray,
        maxWidth: Float = DEFAULT_MAX_WIDTH,
        maxHeight: Float = DEFAULT_MAX_HEIGHT,
    ): ByteArray
}