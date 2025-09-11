package com.mifos.feature.client.utils

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import androidx.core.content.FileProvider
import co.touchlab.kermit.Logger
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
import java.io.File

@Throws
actual suspend fun openPdfWithDefaultExternalApp(platformFile: PlatformFile) {
    val context = FileKit.context

    try {
        val fileInCache = File(ensurePdfIsInCache(platformFile).path)


        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            fileInCache
        )
        val mimeType = getMimeTypeFromPlatformFile(platformFile)
        val intent = Intent(ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)

            addFlags(FLAG_GRANT_READ_URI_PERMISSION or FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Logger.e(e) { "Failed to open document" }
        throw Exception("Failed to load the document. Reason: ${e.message}")
    }
}


private suspend fun ensurePdfIsInCache(platformFile: PlatformFile): PlatformFile {
    val inputFile = platformFile
    val cacheDir = FileKitUtil.appCache

    if (inputFile.absolutePath().startsWith(cacheDir.absolutePath())) {
        Logger.d { "File is already in cache. Using it directly." }
        return inputFile
    }

    Logger.d { "File is outside cache. Copying it." }
    val finalState = FileKitUtil.writeFileToCache(
        "attachment",
        "pdf",
        platformFile.readBytes()
    ).last()

    return when (finalState) {
        is DataState.Success<*> -> finalState.data
            ?: throw IllegalStateException("File writing succeeded but returned invalid data.")
        is DataState.Error<*> -> throw finalState.exception as? Exception
            ?: Exception("An unknown error occurred while caching the file.")
        DataState.Loading -> throw IllegalStateException("File writing flow finished unexpectedly in a Loading state.")
    }
}