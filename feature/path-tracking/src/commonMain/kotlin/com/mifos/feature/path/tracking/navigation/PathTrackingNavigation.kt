/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.path.tracking.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.path.tracking.PathTrackingScreen
import kotlinx.serialization.Serializable

@Serializable
data object PathTrackingScreenRoute

fun NavGraphBuilder.pathTrackingRoute(
    onBackPressed: () -> Unit,
) {
    composable<PathTrackingScreenRoute> {
        PathTrackingScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateToPathTrackingScreen() {
    navigate(PathTrackingScreenRoute)
}
