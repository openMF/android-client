/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.components

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

    fun getNavTitle(route: String?): String {
        return when (route) {
            HomeDestinationsScreen.SearchScreen.route -> "Dashboard" // Override the title
            HomeDestinationsScreen.ClientListScreen.route -> HomeDestinationsScreen.ClientListScreen.title
            HomeDestinationsScreen.CenterListScreen.route -> HomeDestinationsScreen.CenterListScreen.title
            HomeDestinationsScreen.GroupListScreen.route -> HomeDestinationsScreen.GroupListScreen.title
            else -> ""
        }
    }
}
