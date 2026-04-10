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
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.dialogs.FileKitCameraType
import io.github.vinceglb.filekit.dialogs.openCameraPicker
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

val appCache = FileKit.cacheDir
val appPrivateInternalStorage = FileKit.filesDir
val appInternalStorage = FileKit.databasesDir

actual suspend fun platformPickDirectory(): PlatformFile? {
    return FileKit.openDirectoryPicker()
}

actual fun platformWriteFileToCache(
    fileName: String,
    fileExtension: String,
    filesByteArray: ByteArray,
): Flow<DataState<PlatformFile>> = flow {
    val filePath = appCache / "$fileName.$fileExtension"
    filePath.write(filesByteArray)
    emit(filePath)
}.asDataStateFlow()

actual fun platformWriteFileToApplicationPrivateInternalStorage(
    fileName: String,
    fileExtension: String,
    filesByteArray: ByteArray,
): Flow<DataState<PlatformFile?>> = flow {
    val privateInternalStorage = appPrivateInternalStorage / "$fileName.$fileExtension"
    privateInternalStorage.write(filesByteArray)
    emit(privateInternalStorage)
}.asDataStateFlow()

actual fun platformWriteFileToApplicationInternalStorage(
    fileName: String,
    fileExtension: String,
    filesByteArray: ByteArray,
): Flow<DataState<PlatformFile?>> = flow {
    val internalStorage = appInternalStorage / "$fileName.$fileExtension"
    internalStorage.write(filesByteArray)
    emit(internalStorage)
}.asDataStateFlow()

actual fun platformWriteToSelectedDirectory(
    filesByteArray: ByteArray,
    platformFile: PlatformFile,
): Flow<DataState<Unit>> = flow {
    emit(platformFile.write(filesByteArray))
}.asDataStateFlow()

actual suspend fun platformDeleteFile(file: PlatformFile) {
    file.delete(false)
}

actual fun platformTakePhoto(): Flow<DataState<PlatformFile?>> = flow {
    val result = FileKit.openCameraPicker(
        FileKitCameraType.Photo,
    )
    emit(result)
}.asDataStateFlow()
