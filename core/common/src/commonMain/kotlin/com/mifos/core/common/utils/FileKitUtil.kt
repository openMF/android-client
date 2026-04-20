/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.common.utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *  Do not pass the path or absolute path you get by using any picker,
 *  to PlatformFile class to create an instance of PlatformFile.
 *  It won't work.
 *  Use PlatformFile object returned by pickers directly.
 */
object FileKitUtil {
    fun pickFile(
        dialogTitle: String = "",
        extensions: Set<String> = setOf("pdf", "jpeg", "jpg", "png"),
    ): Flow<DataState<PlatformFile?>> = flow {
        val file = FileKit.openFilePicker(
            type = FileKitType.File(extensions),
            mode = FileKitMode.Single,
            title = dialogTitle,
        )
        emit(file)
    }.asDataStateFlow()

    fun pickImage(
        dialogTitle: String = "",
    ): Flow<DataState<PlatformFile?>> = flow {
        val image = FileKit.openFilePicker(
            type = FileKitType.Image,
            mode = FileKitMode.Single,
            title = dialogTitle,
        )
        emit(image)
    }.asDataStateFlow()

    suspend fun pickDirectory(): PlatformFile? = platformPickDirectory()

    /**
     *  Android
     *  filesDir: Maps to context.filesDir, which is the app’s private internal storage
     *  cacheDir: Maps to context.cacheDir, which is the app’s private cache directory
     *  databasesDir: Maps to a databases subdirectory in the app’s internal storage
     *
     *  iOS
     *  filesDir: Maps to the app’s Documents directory, which is backed up with iCloud
     *  cacheDir: Maps to the app’s Caches directory, which isn’t backed up and may be cleared by the system
     *  databasesDir: Maps to a databases subdirectory in the app’s Documents directory
     *
     *  macOS
     *  filesDir: Maps to ~/Library/Application Support/<app-id>/, requiring FileKit initialization with an app ID
     *  cacheDir: Maps to ~/Library/Caches/<app-id>/
     *  databasesDir: Maps to a databases subdirectory in the application support directory
     *
     *  JVM (Desktop)
     *  filesDir: Maps to platform-specific app data locations:
     *  Linux: ~/.local/share/<app-id>/
     *  macOS: ~/Library/Application Support/<app-id>/
     *  Windows: %APPDATA%/<app-id>/
     *
     *  cacheDir: Maps to platform-specific cache locations:
     *  Linux: ~/.cache/<app-id>/
     *  macOS: ~/Library/Caches/<app-id>/
     *  Windows: %LOCALAPPDATA%/<app-id>/Cache/
     *
     *  databasesDir: Maps to a databases subdirectory within filesDir
     */

    fun writeFileToCache(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    ): Flow<DataState<PlatformFile>> = platformWriteFileToCache(fileName, fileExtension, filesByteArray)

    fun writeFileToApplicationPrivateInternalStorage(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    ): Flow<DataState<PlatformFile?>> = platformWriteFileToApplicationPrivateInternalStorage(fileName, fileExtension, filesByteArray)

    // Use only if you are using a database service such as room or sql delight
    fun writeFileToApplicationInternalStorage(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    ): Flow<DataState<PlatformFile?>> = platformWriteFileToApplicationInternalStorage(fileName, fileExtension, filesByteArray)

    fun writeToSelectedDirectory(
        filesByteArray: ByteArray,
        platformFile: PlatformFile,
    ): Flow<DataState<Unit>> = platformWriteToSelectedDirectory(filesByteArray, platformFile)

    suspend fun deleteFile(
        file: PlatformFile,
    ) = platformDeleteFile(file)

    fun takePhoto(): Flow<DataState<PlatformFile?>> = platformTakePhoto()
}

expect suspend fun platformPickDirectory(): PlatformFile?

expect fun platformWriteFileToCache(
    fileName: String,
    fileExtension: String,
    filesByteArray: ByteArray,
): Flow<DataState<PlatformFile>>

expect fun platformWriteFileToApplicationPrivateInternalStorage(
    fileName: String,
    fileExtension: String,
    filesByteArray: ByteArray,
): Flow<DataState<PlatformFile?>>

// Use only if you are using a database service such as room or sqldelight
expect fun platformWriteFileToApplicationInternalStorage(
    fileName: String,
    fileExtension: String,
    filesByteArray: ByteArray,
): Flow<DataState<PlatformFile?>>

expect fun platformWriteToSelectedDirectory(
    filesByteArray: ByteArray,
    platformFile: PlatformFile,
): Flow<DataState<Unit>>

expect suspend fun platformDeleteFile(
    file: PlatformFile,
)

expect fun platformTakePhoto(): Flow<DataState<PlatformFile?>>
