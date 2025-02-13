package com.mifos.core.common.utils

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Foundation.writeToFile

// iOS implementation
@BetaInteropApi
@OptIn(ExperimentalForeignApi::class)
actual fun createPlatformFileUtils(): FileUtils = object : FileUtils {
    override suspend fun writeInputStreamDataToFile(
        inputStream: ByteArray,
        filePath: String,
    ): Boolean =
        withContext(Dispatchers.Default) {
            try {
                val nsData = inputStream.toNSData()
                nsData.writeToFile(filePath, true)
                true
            } catch (e: Exception) {
                FileUtils.logger.w { "File operations not supported in nativeIOS" }
                false
            }
        }

    @BetaInteropApi
    fun ByteArray.toNSData(): NSData = memScoped {
        NSData.create(bytes = allocArrayOf(this@toNSData), length = this@toNSData.size.toULong())
    }
}