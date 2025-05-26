package cmp.navigation.components

import cmp.navigation.navigation.HomeDestinationsScreen

object NavigationConstants {

    private val NavScreenRoutes = listOf(
        HomeDestinationsScreen.SearchScreen.route,
        HomeDestinationsScreen.ClientListScreen.route,
        HomeDestinationsScreen.CenterListScreen.route,
        HomeDestinationsScreen.GroupListScreen.route,
    )

    fun isNavScreen(route: String?): Boolean {
        return NavScreenRoutes.contains(route)
    }
}