/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.path.tracking.ui

import androidx.compose.runtime.Composable
import com.mifos.core.model.objects.users.UserLatLng

/**
 * Wasm / browser renders the shared list UI. Map preview + "open in maps"
 * stubbed pending a cross-platform map primitive.
 */
@Composable
actual fun PathTrackingScreen(
    onBackPressed: () -> Unit,
    viewModel: PathTrackingViewModel,
) {
    PathTrackingScreenBinding(
        viewModel = viewModel,
        onBackPressed = onBackPressed,
        openInMaps = { /* no-op: web map link pending */ },
    )
}

@Composable
actual fun PathTrackingMapView(latLngList: List<UserLatLng>) {
    // Map placeholder — cross-platform map primitive pending.
}
