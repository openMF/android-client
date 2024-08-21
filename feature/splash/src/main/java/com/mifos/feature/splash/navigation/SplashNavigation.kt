package com.mifos.feature.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.splash.splash.SplashScreen

fun NavGraphBuilder.splashNavGraph(
    navigatePasscode: () -> Unit,
    navigateLogin: () -> Unit
) {
    navigation(
        startDestination = SplashScreens.SplashScreen.route,
        route = SplashScreens.SplashScreenRoute.route,
    ) {
        splashScreenRoute(
            navigateLogin = navigateLogin,
            navigatePasscode = navigatePasscode
        )
    }
}

fun NavGraphBuilder.splashScreenRoute(
    navigatePasscode: () -> Unit,
    navigateLogin: () -> Unit
) {
    composable(
        route = SplashScreens.SplashScreen.route,
    ) {
        SplashScreen(
            navigatePasscode = navigatePasscode,
            navigateLogin = navigateLogin
        )
    }
}