/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mifos.feature.auth.navigation.authNavGraph
import com.mifos.feature.auth.navigation.navigateToLogin
import com.mifos.feature.splash.navigation.SplashScreens
import com.mifos.feature.splash.navigation.splashNavGraph
import com.mifos.mifosxdroid.navigation.homeGraph
import com.mifos.mifosxdroid.navigation.navigateHome
import com.mifos.mifosxdroid.navigation.passcodeNavGraph
import org.mifos.library.passcode.navigateToPasscodeScreen

@Composable
fun AndroidClient() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashScreens.SplashScreenRoute.route,
    ) {
        splashNavGraph(
            navigatePasscode = navController::navigateHome,
            navigateLogin = navController::navigateToLogin,
        )

        passcodeNavGraph(
            navController = navController,
        )

        authNavGraph(
            navigatePasscode = navController::navigateToPasscodeScreen,
            navigateHome = navController::navigateHome,
            updateServerConfig = {},
        )

        homeGraph()
    }
}
