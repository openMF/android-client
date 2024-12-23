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

import com.mifos.mifosxdroid.HomeDestinationsScreen

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
