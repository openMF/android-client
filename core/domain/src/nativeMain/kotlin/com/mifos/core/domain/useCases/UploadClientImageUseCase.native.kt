/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import platform.Foundation.NSData
import platform.Foundation.create

actual class PlatformFile(private val path: String) {
    actual fun toMultipartData(): MultipartData {
        val data: NSData? = NSData.create(contentsOfFile = path)
        return MultipartData(data)
    }
}

actual class MultipartData(val data: NSData?)
