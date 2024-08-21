package com.mifos.feature.auth.navigation

sealed class AuthScreens(val route: String) {

    data object LoginScreenRoute : AuthScreens("login_screen_route")

    data object LoginScreen : AuthScreens("login_screen")

}