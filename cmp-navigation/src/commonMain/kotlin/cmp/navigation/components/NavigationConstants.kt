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
import cmp.navigation.utils.toObjectNavigationRoute
import com.mifos.feature.center.navigation.CenterListRoute
import com.mifos.feature.client.navigation.ClientListScreenRoute
import com.mifos.feature.groups.navigation.GroupListRoute
import com.mifos.feature.search.navigation.SearchScreenRoute

object NavigationConstants {

    private val navScreenRoutes = listOf(
        SearchScreenRoute.toObjectNavigationRoute(),
        ClientListScreenRoute.toObjectNavigationRoute(),
        CenterListRoute.toObjectNavigationRoute(),
        GroupListRoute.toObjectNavigationRoute(),
    )

    fun isNavScreen(route: String?): Boolean {
        return navScreenRoutes.contains(route)
    }

    fun getTitleForRoute(route: String?): String {
        return when (route) {
            ClientListScreenRoute.toObjectNavigationRoute() -> HomeDestinationsScreen.ClientListScreen.title
            CenterListRoute.toObjectNavigationRoute() -> HomeDestinationsScreen.CenterListScreen.title
            GroupListRoute.toObjectNavigationRoute() -> HomeDestinationsScreen.GroupListScreen.title
            else -> "Dashboard"
        }
    }
}
