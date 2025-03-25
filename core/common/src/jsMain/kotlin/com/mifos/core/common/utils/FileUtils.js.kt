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

actual fun createPlatformFileUtils(): FileUtils = object : FileUtils {
    override suspend fun writeInputStreamDataToFile(
        inputStream: ByteArray,
        filePath: String,
    ): Boolean {
        FileUtils.logger.w { "File operations not supported in JS/Wasm" }
        return false
    }
}
