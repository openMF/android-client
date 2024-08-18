package com.mifos.feature.activate.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mifos.core.common.utils.Constants
import com.mifos.feature.activate.ActivateScreen

/**
 * Created by Pronay Sarker on 18/08/2024 (1:40 PM)
 */
fun NavGraphBuilder.activateNavGraph(
    onBackPressed: () -> Unit
) {
    composable(
        route = ActivateScreens.ActivateScreen.route,
        arguments = listOf(
            navArgument(name = Constants.ACTIVATE_ID, builder = { NavType.IntType }),
            navArgument(name = Constants.ACTIVATE_TYPE, builder = { NavType.StringType })
        )
    ) {
        ActivateScreen(
            onBackPressed = onBackPressed
        )
    }
}

fun NavController.navigateToActivateScreen(id : Int, type : String){
    navigate(ActivateScreens.ActivateScreen.argument(id, type))
}