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

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *  Do not pass the path or absolute path you get by using any picker,
 *  to PlatformFile class to create an instance of PlatformFile.
 *  It won't work.
 *  Use PlatformFile object returned by pickers directly.
 */
object FileKitUtil {

    val appCache = FileKit.cacheDir
    val appPrivateInternalStorage = FileKit.filesDir
    val appInternalStorage = FileKit.databasesDir

    fun pickFile(
        dialogTitle: String = "",
        extensions: Set<String> = setOf("pdf", "jpeg", "jpg", "png"),
    ) = flow {
        val file = FileKit.openFilePicker(
            type = FileKitType.File(extensions),
            mode = FileKitMode.Single,
            title = dialogTitle,
        )
        emit(file)
    }.asDataStateFlow()

    fun pickImage(
        dialogTitle: String = "",
    ) = flow {
        val image = FileKit.openFilePicker(
            type = FileKitType.Image,
            mode = FileKitMode.Single,
            title = dialogTitle,
        )
        emit(image)
    }.asDataStateFlow()

    suspend fun pickDirectory(): PlatformFile? {
        return FileKit.openDirectoryPicker()
    }

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
    ) = flow {
        val filePath = appCache / "$fileName.$fileExtension"
        filePath.write(filesByteArray)
        emit(filePath)
    }.asDataStateFlow()

    fun writeFileToApplicationPrivateInternalStorage(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    ) = flow {
        val privateInternalStorage = appPrivateInternalStorage / "$fileName.$fileExtension"
        privateInternalStorage.write(filesByteArray)
        emit(privateInternalStorage)
    }.asDataStateFlow()

    // Use only if you are using a database service such as room or sqldelight
    fun writeFileToApplicationInternalStorage(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    ) = flow {
        val internalStorage = appInternalStorage / "$fileName.$fileExtension"
        internalStorage.write(filesByteArray)
        emit(internalStorage)
    }.asDataStateFlow()

    fun writeToSelectedDirectory(
        filesByteArray: ByteArray,
        platformFile: PlatformFile,
    ) = flow {
        emit(platformFile.write(filesByteArray))
    }.asDataStateFlow()

    suspend fun deleteFile(
        file: PlatformFile,
    ) {
        file.delete(false)
    }

    fun takePhoto() = takePhotoIfSupported()
}

expect fun takePhotoIfSupported(): Flow<DataState<PlatformFile?>>
