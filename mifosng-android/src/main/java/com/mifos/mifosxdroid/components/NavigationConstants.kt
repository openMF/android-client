package com.mifos.mifosxdroid.components

import com.mifos.mifosxdroid.Screens

object NavigationConstants {

    private val NavScreenRoutes = listOf(
        Screens.SearchScreen.route,
        Screens.ClientListScreen.route,
        Screens.CenterListScreen.route,
        Screens.GroupListScreen.route,
    )


    fun isNavScreen(route: String?): Boolean {
        return NavScreenRoutes.contains(route)
    }

}