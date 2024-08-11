package com.mifos.feature.path_tracking.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.path_tracking.PathTrackingScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (7:35 AM)
 */
const val PATH_TRACKING_SCREEN_ROUTE = "path_tracking_route"

fun NavController.navigateToPathTrackingScreen() {
    this.navigate(PATH_TRACKING_SCREEN_ROUTE)
}

fun NavGraphBuilder.pathTrackingScreen(
    onBackPressed: () -> Unit
) {
    composable(PATH_TRACKING_SCREEN_ROUTE) {
        PathTrackingScreen(
            onBackPressed = onBackPressed,
            onPathTrackingClick = {}
        )
    }
}