/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.utils

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import kpt.feature.client.generated.resources.Res
import kpt.feature.client.generated.resources.client_documents_failed_to_open
import kpt.feature.client.generated.resources.default_preview_pdf_name
import androidx.core.content.FileProvider
import com.mifos.core.ui.util.getMimeTypeFromPlatformFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.context
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import org.jetbrains.compose.resources.getString
import java.io.File

@Throws
actual suspend fun openPdfWithDefaultExternalApp(platformFile: PlatformFile) {
    val context = FileKit.context

    try {
        val fileInCache = File(ensurePdfIsInCache(platformFile).path)

        // Do no change authority value, if you don't know what you are doing.
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            fileInCache,
        )
        val mimeType = getMimeTypeFromPlatformFile(platformFile.extension)
        val intent = Intent(ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)

            addFlags(FLAG_GRANT_READ_URI_PERMISSION or FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        throw Exception(getString(Res.string.client_documents_failed_to_open) + "${e.message}")
    }
}

private suspend fun ensurePdfIsInCache(platformFile: PlatformFile): PlatformFile {
    val inputFile = platformFile
    val cacheDir = FileKit.cacheDir

    if (inputFile.absolutePath().startsWith(cacheDir.absolutePath())) {
        return inputFile
    }

    return writeFileToCache(
        getString(Res.string.default_preview_pdf_name),
        "pdf",
        platformFile.readBytes(),
    ).last()
}

fun writeFileToCache(
    fileName: String,
    fileExtension: String,
    filesByteArray: ByteArray,
) = flow {
    val filePath = FileKit.cacheDir / "$fileName.$fileExtension"
    filePath.write(filesByteArray)
    emit(filePath)
}
