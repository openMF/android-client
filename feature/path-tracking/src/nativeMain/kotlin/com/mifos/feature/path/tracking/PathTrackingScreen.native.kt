/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.path.tracking

import androidx.compose.runtime.Composable
import com.mifos.core.model.objects.users.UserLatLng
import com.mifos.feature.pathTracking.PathTrackingViewModel

@Composable
actual fun PathTrackingScreen(
    onBackPressed: () -> Unit,
    viewModel: PathTrackingViewModel,
) {
    TODO("Not yet implemented")
}

@Composable
actual fun HandleLocationPermissionRequest(
    show: Boolean,
    onPermissionResult: (granted: Boolean) -> Unit,
) {
    if (show) {
        onPermissionResult(true)
    }
}

@Composable
actual fun PathTrackingMapView(latLngList: List<UserLatLng>) {
    TODO("Not yet implemented")
}
