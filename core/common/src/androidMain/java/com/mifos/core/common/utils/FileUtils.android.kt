package com.mifos.core.common.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual fun createPlatformFileUtils(): FileUtils = AndroidFileUtils()

class AndroidFileUtils : FileUtils {
    override suspend fun writeInputStreamDataToFile(
        inputStream: ByteArray,
        filePath: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            File(filePath).writeBytes(inputStream)
            true
        } catch (e: Exception) {
            FileUtils.logger.e { "Android write error: ${e.message}" }
            false
        }
    }
}