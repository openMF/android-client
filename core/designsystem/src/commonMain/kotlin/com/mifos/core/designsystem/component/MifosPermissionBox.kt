/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.designsystem.component

import androidx.compose.runtime.Composable

@Composable
expect fun PermissionBox(
    requiredPermissions: List<String>,
    title: String,
    confirmButtonText: String,
    dismissButtonText: String,
    description: String? = null,
    onGranted: @Composable (() -> Unit)? = null,
)

expect fun getRequiredPermissionsForExport(): List<String>

expect fun getRequiredPermissionsForLocation(): List<String>
