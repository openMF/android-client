/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.utils

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import io.github.vinceglb.filekit.dialogs.compose.PhotoResultLauncher

@OptIn(ExperimentalPermissionsApi::class)
actual class PlatformCameraLauncher
internal constructor(
    private val permissionState: PermissionState,
    private val launcher: PhotoResultLauncher,
) {
    actual fun launch() {
        if (permissionState.status.isGranted) {
            launcher.launch()
        } else {
            permissionState.launchPermissionRequest()
        }
    }
}
