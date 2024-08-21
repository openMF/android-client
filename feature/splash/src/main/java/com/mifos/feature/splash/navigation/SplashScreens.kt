package com.mifos.feature.splash.navigation

sealed class SplashScreens(val route: String) {

    data object SplashScreenRoute : SplashScreens("splash_screen_route")

    data object SplashScreen : SplashScreens("splash_screen")

}