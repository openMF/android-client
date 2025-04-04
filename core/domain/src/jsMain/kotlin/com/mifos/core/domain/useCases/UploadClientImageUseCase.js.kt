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

import org.w3c.files.File
import org.w3c.xhr.FormData

actual class PlatformFile(private val file: File) {
    actual fun toMultipartData(): MultipartData {
        val formData = FormData()
        formData.append("file", file)
        return MultipartData(formData)
    }
}

actual class MultipartData(val formData: FormData)
