package com.mifos.feature.path_tracking.navigation

sealed class PathTrackingScreens(val route: String) {

    data object PathTrackingScreenRoute : PathTrackingScreens("path_tracking_screen_route")

    data object PathTrackingScreen : PathTrackingScreens("path_tracking_screen")

}