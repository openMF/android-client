/*
 * Copyright 2026 Mifos Initiative
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
import io.github.vinceglb.filekit.download
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual suspend fun platformPickDirectory(): PlatformFile? {
    // not support in Js
    return null
}

actual fun platformWriteFileToCache(
    fileName: String,
    fileExtension: String,
    filesByteArray: ByteArray,
): Flow<DataState<PlatformFile>> = flow {
    emit(DataState.Error(IllegalStateException("Platform not supported")))
}

actual fun platformWriteFileToApplicationPrivateInternalStorage(
    fileName: String,
    fileExtension: String,
    filesByteArray: ByteArray,
): Flow<DataState<PlatformFile?>> = flow {
    FileKit.download(bytes = filesByteArray, fileName = "$fileName.$fileExtension")
    emit(null)
}.asDataStateFlow()

actual fun platformWriteFileToApplicationInternalStorage(
    fileName: String,
    fileExtension: String,
    filesByteArray: ByteArray,
): Flow<DataState<PlatformFile?>> = flow {
    FileKit.download(bytes = filesByteArray, fileName = "$fileName.$fileExtension")
    emit(null)
}.asDataStateFlow()

actual fun platformWriteToSelectedDirectory(
    filesByteArray: ByteArray,
    platformFile: PlatformFile,
): Flow<DataState<Unit>> = flow {
    emit(FileKit.download(bytes = filesByteArray, fileName = "${platformFile.name}.${platformFile.extension}"))
}.asDataStateFlow()

actual suspend fun platformDeleteFile(file: PlatformFile) {
    // not support in JS.
}

actual fun platformTakePhoto(): Flow<DataState<PlatformFile?>> = flow {
    emit(DataState.Error(IllegalStateException("Platform not supported")))
}
