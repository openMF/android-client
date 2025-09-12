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

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_documents_failed_to_open
import androidclient.feature.client.generated.resources.default_preview_pdf_name
import androidclient.feature.client.generated.resources.returned_invalid_data_after_caching
import androidclient.feature.client.generated.resources.unexpected_loading
import androidx.core.content.FileProvider
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileKitUtil
import com.mifos.core.ui.util.getMimeTypeFromPlatformFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.context
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
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
        val mimeType = getMimeTypeFromPlatformFile(platformFile)
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
    val cacheDir = FileKitUtil.appCache

    if (inputFile.absolutePath().startsWith(cacheDir.absolutePath())) {
        return inputFile
    }

    val finalState = FileKitUtil.writeFileToCache(
        getString(Res.string.default_preview_pdf_name),
        "pdf",
        platformFile.readBytes(),
    ).last()

    return when (finalState) {
        is DataState.Success<*> ->
            finalState.data
                ?: throw IllegalStateException(getString(Res.string.returned_invalid_data_after_caching))
        is DataState.Error<*> ->
            throw Exception(finalState.exception)
        DataState.Loading -> throw IllegalStateException(getString(Res.string.unexpected_loading))
    }
}
