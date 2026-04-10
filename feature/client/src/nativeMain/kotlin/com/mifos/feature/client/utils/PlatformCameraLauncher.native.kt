/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.utils

import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.PhotoResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher

actual class PlatformCameraLauncher
internal constructor(
    private val launcher: PhotoResultLauncher,
) {
    actual fun launch() {
        launcher.launch()
    }
}

@Composable
actual fun rememberPlatformCameraLauncher(onImageCapturedPath: (PlatformFile?) -> Unit): PlatformCameraLauncher {
    val launcher = rememberCameraPickerLauncher { file ->
        onImageCapturedPath(file)
    }

    return PlatformCameraLauncher(
        launcher = launcher,
    )
}
