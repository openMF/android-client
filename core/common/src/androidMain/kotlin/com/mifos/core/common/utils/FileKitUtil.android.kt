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
import io.github.vinceglb.filekit.dialogs.FileKitCameraType
import io.github.vinceglb.filekit.dialogs.openCameraPicker
import io.github.vinceglb.filekit.exceptions.FileKitException
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.flow.flow

actual suspend fun takePhotoIfSupported() = flow {
    emit(DataState.Loading)
    val result = try {
        val image = FileKit.openCameraPicker(
            FileKitCameraType.Photo,
        )
        if (image == null) {
            DataState.Error(IllegalStateException("Failed to load image"))
        } else {
            DataState.Success(image.path)
        }
    } catch (fileException: FileKitException) {
        DataState.Error(fileException)
    } catch (e: Exception) {
        DataState.Error(e)
    }
    emit(result)
}
