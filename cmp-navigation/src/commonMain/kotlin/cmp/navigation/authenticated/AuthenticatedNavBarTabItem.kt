/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation.authenticated

import androidx.compose.ui.graphics.vector.ImageVector
import cmp.navigation.utils.toObjectNavigationRoute
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.feature.center.navigation.CenterListRoute
import com.mifos.feature.client.navigation.ClientListScreenRoute
import com.mifos.feature.groups.navigation.GroupListRoute
import com.mifos.feature.search.navigation.SearchScreenRoute
import org.jetbrains.compose.resources.StringResource
import org.mifos.navigation.generated.resources.Res
import org.mifos.navigation.generated.resources.cmp_navigation_center
import org.mifos.navigation.generated.resources.cmp_navigation_clients
import org.mifos.navigation.generated.resources.cmp_navigation_groups
import org.mifos.navigation.generated.resources.cmp_navigation_search

sealed class AuthenticatedNavBarTabItem : NavigationItem {

    data object SearchTab : AuthenticatedNavBarTabItem() {
        override val iconResSelected: ImageVector
            get() = MifosIcons.Dashboard
        override val iconRes: ImageVector
            get() = MifosIcons.Dashboard
        override val labelRes: StringResource
            get() = Res.string.cmp_navigation_search
        override val contentDescriptionRes: StringResource
            get() = Res.string.cmp_navigation_search
        override val graphRoute: String
            get() = SearchScreenRoute.toObjectNavigationRoute()
        override val startDestinationRoute: String
            get() = SearchScreenRoute.toObjectNavigationRoute()
        override val testTag: String
            get() = "Search Tab"
    }

    data object ClientTab : AuthenticatedNavBarTabItem() {
        override val iconResSelected: ImageVector
            get() = MifosIcons.Person
        override val iconRes: ImageVector
            get() = MifosIcons.Person
        override val labelRes: StringResource
            get() = Res.string.cmp_navigation_clients
        override val contentDescriptionRes: StringResource
            get() = Res.string.cmp_navigation_clients
        override val graphRoute: String
            get() = ClientListScreenRoute.toObjectNavigationRoute()
        override val startDestinationRoute: String
            get() = ClientListScreenRoute.toObjectNavigationRoute()
        override val testTag: String
            get() = "Client Tab"
    }

    data object CentersTab : AuthenticatedNavBarTabItem() {
        override val iconResSelected: ImageVector
            get() = MifosIcons.Business
        override val iconRes: ImageVector
            get() = MifosIcons.Business
        override val labelRes: StringResource
            get() = Res.string.cmp_navigation_center
        override val contentDescriptionRes: StringResource
            get() = Res.string.cmp_navigation_center
        override val graphRoute: String
            get() = CenterListRoute.toObjectNavigationRoute()
        override val startDestinationRoute: String
            get() = CenterListRoute.toObjectNavigationRoute()
        override val testTag: String
            get() = "Center Tab"
    }

    data object GroupsTab : AuthenticatedNavBarTabItem() {
        override val iconResSelected: ImageVector
            get() = MifosIcons.Group
        override val iconRes: ImageVector
            get() = MifosIcons.Group
        override val labelRes: StringResource
            get() = Res.string.cmp_navigation_groups
        override val contentDescriptionRes: StringResource
            get() = Res.string.cmp_navigation_groups
        override val graphRoute: String
            get() = GroupListRoute.toObjectNavigationRoute()
        override val startDestinationRoute: String
            get() = GroupListRoute.toObjectNavigationRoute()
        override val testTag: String
            get() = "Group Tab"
    }
}

/**
 * Represents a user-interactable item to navigate a user via the bottom app bar or navigation rail.
 */
interface NavigationItem {
    /**
     * The resource ID for the icon representing the tab when it is selected.
     */
    val iconResSelected: ImageVector

    /**
     * Resource id for the icon representing the tab.
     */
    val iconRes: ImageVector

    /**
     * Resource id for the label describing the tab.
     */
    val labelRes: StringResource

    /**
     * Resource id for the content description describing the tab.
     */
    val contentDescriptionRes: StringResource

    /**
     * Route of the tab's graph.
     */
    val graphRoute: String

    /**
     * Route of the tab's start destination.
     */
    val startDestinationRoute: String

    /**
     * The test tag of the tab.
     */
    val testTag: String
}
