package com.mifos.mifosxdroid

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mifos.feature.auth.navigation.authNavGraph
import com.mifos.feature.auth.navigation.navigateToLogin
import com.mifos.feature.passcode.navigation.passcodeNavGraph
import com.mifos.feature.splash.navigation.SplashScreens
import com.mifos.feature.splash.navigation.splashNavGraph
import com.mifos.mifosxdroid.navigation.HomeScreens
import com.mifos.mifosxdroid.navigation.homeGraph
import com.mifos.mifosxdroid.navigation.navigateHome

@Composable
fun AndroidClient() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashScreens.SplashScreenRoute.route
    ) {
        splashNavGraph(
            navigatePasscode = navController::navigateHome,
            navigateLogin = navController::navigateToLogin
        )

        passcodeNavGraph(
            navController = navController
        )

        authNavGraph(
            navigatePasscode = {},
            navigateHome = navController::navigateHome,
            updateServerConfig = {}
        )

        homeGraph()
    }
}