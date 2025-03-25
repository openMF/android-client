/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.common.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual fun createPlatformFileUtils(): FileUtils = AndroidFileUtils()

class AndroidFileUtils : FileUtils {
    override suspend fun writeInputStreamDataToFile(
        inputStream: ByteArray,
        filePath: String,
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
