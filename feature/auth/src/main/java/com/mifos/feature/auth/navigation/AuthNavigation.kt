package com.mifos.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.auth.login.LoginScreen

fun NavGraphBuilder.authNavGraph(
    navigateHome: () -> Unit,
    navigatePasscode: () -> Unit,
    updateServerConfig: () -> Unit
) {
    navigation(
        startDestination = AuthScreens.LoginScreen.route,
        route = AuthScreens.LoginScreenRoute.route,
    ) {
        loginRoute(
            navigatePasscode = navigatePasscode,
            navigateHome = navigateHome,
            updateServerConfig = updateServerConfig
        )
    }
}

private fun NavGraphBuilder.loginRoute(
    navigateHome: () -> Unit,
    navigatePasscode: () -> Unit,
    updateServerConfig: () -> Unit
) {
    composable(
        route = AuthScreens.LoginScreen.route,
    ) {
        LoginScreen(
            homeIntent = navigateHome,
            passcodeIntent = navigatePasscode,
            onClickToUpdateServerConfig = updateServerConfig
        )
    }
}

fun NavController.navigateToLogin() {
    navigate(AuthScreens.LoginScreen.route)
}
