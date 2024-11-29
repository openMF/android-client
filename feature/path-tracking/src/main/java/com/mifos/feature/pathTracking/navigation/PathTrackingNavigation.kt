/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.pathTracking.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.pathTracking.PathTrackingScreen

fun NavGraphBuilder.pathTrackingNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = PathTrackingScreens.PathTrackingScreen.route,
        route = PathTrackingScreens.PathTrackingScreenRoute.route,
    ) {
        pathTrackingRoute(
            onBackPressed = navController::popBackStack,
        )
    }
}

fun NavGraphBuilder.pathTrackingRoute(
    onBackPressed: () -> Unit,
) {
    composable(
        route = PathTrackingScreens.PathTrackingScreen.route,
    ) {
        PathTrackingScreen(
            onBackPressed = onBackPressed,
        )
    }
}
