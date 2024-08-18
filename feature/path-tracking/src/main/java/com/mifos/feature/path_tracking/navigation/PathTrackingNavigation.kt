package com.mifos.feature.path_tracking.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.path_tracking.PathTrackingScreen

fun NavGraphBuilder.pathTrackingNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = PathTrackingScreens.PathTrackingScreen.route,
        route = PathTrackingScreens.PathTrackingScreenRoute.route
    ) {
        pathTrackingRoute(
            onBackPressed = navController::popBackStack
        )
    }
}

fun NavGraphBuilder.pathTrackingRoute(
    onBackPressed: () -> Unit
) {
    composable(
        route = PathTrackingScreens.PathTrackingScreen.route
    ) {
        PathTrackingScreen(
            onBackPressed = onBackPressed
        )
    }
}