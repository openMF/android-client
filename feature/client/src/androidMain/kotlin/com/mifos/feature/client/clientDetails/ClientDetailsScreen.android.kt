/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientDetails

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.mifos.feature.client.utils.PlatformCameraLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun rememberPlatformCameraLauncher(
    clientId: Int,
    viewModel: ClientDetailsViewModel,
): PlatformCameraLauncher {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val launcher = rememberCameraPickerLauncher { file ->
        file?.let { viewModel.saveClientImage(clientId, it) }
    }

    return PlatformCameraLauncher(
        permissionState = permissionState,
        launcher = launcher,
    )
}
