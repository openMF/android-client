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
import io.github.vinceglb.filekit.absolutePath
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
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
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
        emit(DataState.Loading)
        try {
            val file= FileKit.openFilePicker(
                type = FileKitType.File(".pdf"),
                mode = FileKitMode.Single,
                title = dialogTitle,
                dialogSettings = FileKitDialogSettings.createDefault(),
            )
            if(file==null){
                emit(DataState.Error(IllegalStateException("Failed to load file")))
            } else{
                val result = writeFileToCache(
                    file.nameWithoutExtension,
                    file.extension,
                    file.readBytes()
                )
                emit(result)
            }
        } catch (fileException: FileKitException){
            emit(DataState.Error(fileException))
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    fun pickImageAndSaveToCache(
        dialogTitle: String = "",
    ) = flow {
        emit(DataState.Loading)
        try {
            val image= FileKit.openFilePicker(
                type = FileKitType.Image,
                mode = FileKitMode.Single,
                title = dialogTitle,
            )
            if(image==null){
                emit(DataState.Error(IllegalStateException("Failed to load file")))
            } else{
                val imagBytes = image.readBytes()
                val result= writeFileToCache(
                    image.nameWithoutExtension,
                    image.extension,
                    imagBytes
                )
                emit(result)
            }
        } catch (fileException: FileKitException){
            emit(DataState.Error(fileException))
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    fun loadFile(
        absolutePath: String,
    ) = flow {
        emit(DataState.Loading)
        try {
            val file = PlatformFile(absolutePath)
            val fileBytes = file.readBytes()
            emit(DataState.Success(fileBytes))
        } catch (fileException: FileKitException){
            emit(DataState.Error(fileException))
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

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

    suspend fun writeFileToCache(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    )= try {
        val cacheDir = appCache/"${fileName}.${fileExtension}"
        cacheDir.write(filesByteArray)
        DataState.Success(cacheDir.absolutePath())
    } catch (fileException: FileKitException) {
        DataState.Error(fileException)
    } catch (e: Exception) {
        DataState.Error(e)
    }


    suspend fun writeFileToApplicationPrivateInternalStorage(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    ) =  try {
        val privateInternalStorage = appPrivateInternalStorage/"${fileName}.${fileExtension}"
        privateInternalStorage.write(filesByteArray)
        DataState.Success(privateInternalStorage.absolutePath())
    } catch (fileException: FileKitException) {
        DataState.Error(fileException)
    } catch (e: Exception) {
        DataState.Error(e)
    }


    // Use only if you are using a database service such as room or sqldelight
    suspend fun writeFileToApplicationInternalStorage(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    ) = try {
        val internalStorage =  appInternalStorage/"${fileName}.${fileExtension}"
        internalStorage.write(filesByteArray)
        DataState.Success(internalStorage.absolutePath())
    } catch (fileException: FileKitException) {
        DataState.Error(fileException)
    } catch (e: Exception) {
        DataState.Error(e)
    }


    suspend fun writeToSelectedDirectory(
        fileName: String,
        fileExtension: String,
        filesByteArray: ByteArray,
    ): DataState<String> {
        val directory = pickDirectory()

        return if (directory == null) {
            DataState.Error(IllegalStateException("Failed to pick file directory"))
        } else {
            val filePath = directory / "$fileName.$fileExtension"

            try {
                filePath.write(filesByteArray)
                DataState.Success(filePath.absolutePath())
            } catch (fileException: FileKitException) {
                DataState.Error(fileException)
            } catch (e: Exception) {
                DataState.Error(e)
            }
        }
    }


    suspend fun deleteFile(
        file: PlatformFile,
    ) {
        file.delete(false)
    }

    suspend fun takePhoto() = takePhotoIfSupported()

}

expect suspend fun takePhotoIfSupported(): Flow<DataState<String>>