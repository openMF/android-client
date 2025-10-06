/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.utils

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

internal suspend fun createDocumentRequestBody(
    documentFile: PlatformFile,
): MultiPartFormDataContent {
    val byteArray = documentFile.readBytes()
    return MultiPartFormDataContent(
        formData {
            append(
                "file",
                byteArray,
                Headers.build {
                    append(HttpHeaders.ContentType, getMimeType(documentFile.extension))
                    append(HttpHeaders.ContentDisposition, "filename=\"${documentFile.name}\"")
                },
            )
        },
    )
}

internal suspend fun createDocumentRequestBody(
    documentFile: PlatformFile,
    name: String,
    description: String,
): MultiPartFormDataContent {
    val byteArray = documentFile.readBytes()
    return MultiPartFormDataContent(
        formData {
            append(
                "file",
                byteArray,
                Headers.build {
                    append(HttpHeaders.ContentType, getMimeType(documentFile.extension))
                    append(HttpHeaders.ContentDisposition, "filename=\"${documentFile.name}\"")
                },
            )
            append("name", name)
            append("description", description)
        },
    )
}

private fun getMimeType(extension: String): String = when (extension.lowercase()) {
    "jpg", "jpeg" -> "image/jpeg"
    "png" -> "image/png"
    "pdf" -> "application/pdf"
    else -> "application/octet-stream"
}
