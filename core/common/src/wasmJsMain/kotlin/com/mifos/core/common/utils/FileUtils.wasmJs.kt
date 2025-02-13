package com.mifos.core.common.utils

actual fun createPlatformFileUtils(): FileUtils = object : FileUtils {
    override suspend fun writeInputStreamDataToFile(
        inputStream: ByteArray,
        filePath: String
    ): Boolean {
        FileUtils.logger.w { "File operations not supported in JS/Wasm" }
        return false
    }
}