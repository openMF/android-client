/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.util

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.InternalAPI

@OptIn(InternalAPI::class)
suspend fun multipartRequestBody(
    file: PlatformFile,
    name: String? = null,
    description: String? = null,
): MultiPartFormDataContent {
    val mimeType = getMimeTypeFromPlatformFile(file)
    val byteArray = file.readBytes()
    return MultiPartFormDataContent(
        formData {
            append(
                "file",
                byteArray,
                Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                },
            )
            name?.let { append("name", it) }
            description?.let { append("description", it) }
        },
    )
}

fun getMimeTypeFromPlatformFile(file: PlatformFile): String {
    return when (file.extension.lowercase()) {
        "jpeg", "jpg" -> "image/jpeg"
        "png" -> "image/png"
        "pdf" -> "application/pdf"
        "doc" -> "application/msword"
        "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        "xls" -> "application/vnd.ms-excel"
        "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        else -> "application/octet-stream"
    }
}
