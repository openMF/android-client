/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createNewClient

import android.Manifest
import android.telephony.PhoneNumberUtils
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.mifos.feature.client.utils.PlatformCameraLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.path

actual object PhoneNumberUtil {
    actual fun isGlobalPhoneNumber(phoneNumber: String): Boolean {
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun rememberPlatformCameraLauncher(
    onImageCapturedPath: (String?) -> Unit,
): PlatformCameraLauncher {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val launcher = rememberCameraPickerLauncher { file ->
        onImageCapturedPath(file?.path)
    }

    return PlatformCameraLauncher(
        permissionState = permissionState,
        launcher = launcher,
    )
}
