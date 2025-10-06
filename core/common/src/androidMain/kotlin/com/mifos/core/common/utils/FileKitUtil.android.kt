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
import kotlinx.coroutines.flow.flow

actual fun takePhotoIfSupported() = flow {
    val result = FileKit.openCameraPicker(
        FileKitCameraType.Photo,
    )
    emit(result)
}.asDataStateFlow()
