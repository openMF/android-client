package com.mifos.feature.passcode.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.feature.passcode.passcode.PasscodeScreen

fun NavGraphBuilder.passcodeNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = PasscodeScreens.PasscodeScreen.route,
        route = "passcode_screen_route"
    ) {
        passcodeScreenRoute()
    }
}

fun NavGraphBuilder.passcodeScreenRoute(

) {
    composable(
        route = PasscodeScreens.PasscodeScreen.route,
        arguments = listOf(
            navArgument(
                name = Constants.PASSCODE_INITIAL_LOGIN,
                builder = { type = NavType.BoolType })
        )
    ) {
        PasscodeScreen(

        )
    }
}