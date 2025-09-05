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
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exceptions.FileKitException
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object FileKitUtil {

    val appCache = FileKit.cacheDir
    val appPrivateInternalStorage = FileKit.filesDir
    val appInternalStorage = FileKit.databasesDir

    fun pickPdfFile(
        dialogTitle: String = "",
    ) = flow {
        val file = FileKit.openFilePicker(
            type = FileKitType.File(".pdf"),
            mode = FileKitMode.Single,
            title = dialogTitle,
            dialogSettings = FileKitDialogSettings.createDefault(),
        )
        emit(file)
    }.asDataStateFlow()

    fun pickImageFile(
        dialogTitle: String = "",
    ) = flow {
        val file = FileKit.openFilePicker(
            type = FileKitType.Image,
            mode = FileKitMode.Single,
            title = dialogTitle,
        )
        emit(file)
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
    ): Flow<DataState<*>> = flow {
        emit(DataState.Loading)

        val cacheDir = PlatformFile(
            appCache,
            "$fileName.$fileExtension",
        )
        val result = try {
            cacheDir.write(filesByteArray)
            DataState.Success(true)
        } catch (fileException: FileKitException) {
            DataState.Error(fileException)
        } catch (e: Exception) {
            DataState.Error(e)
        }

        emit(result)
    }

    fun writeFileToApplicationPrivateInternalStorage(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    ): Flow<DataState<*>> = flow {
        emit(DataState.Loading)
        val privateInternalStorage = PlatformFile(
            appPrivateInternalStorage,
            "$fileName.$fileExtension",
        )

        val result = try {
            privateInternalStorage.write(filesByteArray)
            DataState.Success(true)
        } catch (fileException: FileKitException) {
            DataState.Error(fileException)
        } catch (e: Exception) {
            DataState.Error(e)
        }

        emit(result)
    }

    // Use only if you are using a database service such as room or sqldelight
    suspend fun writeFileToApplicationInternalStorage(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    ): Flow<DataState<*>> = flow {
        emit(DataState.Loading)

        val internalStorage = PlatformFile(
            appInternalStorage,
            "$fileName.$fileExtension",
        )
        val result = try {
            internalStorage.write(filesByteArray)
            DataState.Success(true)
        } catch (fileException: FileKitException) {
            DataState.Error(fileException)
        } catch (e: Exception) {
            DataState.Error(e)
        }

        emit(result)
    }

    fun writeToSelectedDirectory(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    ): Flow<DataState<*>> = flow {
        emit(DataState.Loading)

        val directory = pickDirectory()

        val filePath = directory?.div("$fileName.$fileExtension")

        val result = try {
            filePath?.write(filesByteArray)
            DataState.Success(true)
        } catch (fileException: FileKitException) {
            DataState.Error(fileException)
        } catch (e: Exception) {
            DataState.Error(e)
        }

        emit(result)
    }

    suspend fun deleteFile(
        file: PlatformFile,
    ) {
        file.delete(false)
    }

    fun takePhoto() = flow {
        val imageFile = takePhotoIfSupported()
        emit(imageFile)
    }.asDataStateFlow()
}

expect suspend fun takePhotoIfSupported(): PlatformFile?
