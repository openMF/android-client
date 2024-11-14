package com.mifos.mifosxdroid

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mifos.feature.auth.navigation.authNavGraph
import com.mifos.feature.auth.navigation.navigateToLogin
import com.mifos.feature.splash.navigation.SplashScreens
import com.mifos.feature.splash.navigation.splashNavGraph
import com.mifos.mifosxdroid.navigation.HomeNavigation
import com.mifos.mifosxdroid.navigation.MifosNavGraph
import com.mifos.mifosxdroid.navigation.homeGraph
import com.mifos.mifosxdroid.navigation.navigateHome
import com.mifos.mifosxdroid.navigation.passcodeNavGraph
import org.mifos.library.passcode.navigateToPasscodeScreen

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
            navigatePasscode = navController::navigateToPasscodeScreen,
            navigateHome = navController::navigateHome,
            updateServerConfig = {}
        )

        composable(MifosNavGraph.MAIN_GRAPH){
            HomeNavigation()
        }
        homeGraph()
    }
}