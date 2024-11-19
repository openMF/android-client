/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.splash.splash.SplashScreen

fun NavGraphBuilder.splashNavGraph(
    navigatePasscode: () -> Unit,
    navigateLogin: () -> Unit,
) {
    navigation(
        startDestination = SplashScreens.SplashScreen.route,
        route = SplashScreens.SplashScreenRoute.route,
    ) {
        splashScreenRoute(
            navigateLogin = navigateLogin,
            navigatePasscode = navigatePasscode,
        )
    }
}

fun NavGraphBuilder.splashScreenRoute(
    navigatePasscode: () -> Unit,
    navigateLogin: () -> Unit,
) {
    composable(
        route = SplashScreens.SplashScreen.route,
    ) {
        SplashScreen(
            navigatePasscode = navigatePasscode,
            navigateLogin = navigateLogin,
        )
    }
}
