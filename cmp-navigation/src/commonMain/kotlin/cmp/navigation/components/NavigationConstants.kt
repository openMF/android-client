/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation.components

import cmp.navigation.navigation.HomeDestinationsScreen
import com.mifos.feature.search.navigation.SearchScreens

object NavigationConstants {

    private val NavScreenRoutes = listOf(
        SearchScreens.SearchScreen.route,
        HomeDestinationsScreen.ClientListScreen.route,
        HomeDestinationsScreen.CenterListScreen.route,
        HomeDestinationsScreen.GroupListScreen.route,
    )

    fun isNavScreen(route: String?): Boolean {
        return NavScreenRoutes.contains(route)
    }
    fun getTitleForRoute(route: String?): String {
        return when (route) {
            HomeDestinationsScreen.ClientListScreen.route -> HomeDestinationsScreen.ClientListScreen.title
            HomeDestinationsScreen.CenterListScreen.route -> HomeDestinationsScreen.CenterListScreen.title
            HomeDestinationsScreen.GroupListScreen.route -> HomeDestinationsScreen.GroupListScreen.title
            else -> "Dashboard"
        }
    }
}
