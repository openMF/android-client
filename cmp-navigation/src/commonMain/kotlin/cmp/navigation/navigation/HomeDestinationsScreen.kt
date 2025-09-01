/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import cmp.navigation.utils.toObjectNavigationRoute
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.feature.about.AboutNavRoute
import com.mifos.feature.center.navigation.CenterListRoute
import com.mifos.feature.checker.inbox.task.navigation.CheckerInboxTaskScreenRoute
import com.mifos.feature.client.navigation.ClientListScreenRoute
import com.mifos.feature.groups.navigation.GroupListRoute
import com.mifos.feature.individualCollectionSheet.navigation.IndividualCollectionSheetScreenRoute
import com.mifos.feature.offline.navigation.OfflineDashboardScreenRoute
import com.mifos.feature.path.tracking.navigation.PathTrackingScreenRoute
import com.mifos.feature.report.navigation.RunReportScreenRoute
import com.mifos.feature.search.navigation.SearchScreenRoute
import com.mifos.feature.settings.navigation.SettingsRoute

sealed class HomeDestinationsScreen(
    val title: String = "",
    val route: String,
    val icon: ImageVector? = null,
) {
    data object CheckerInboxAndTasksScreen : HomeDestinationsScreen(
        title = "Checker Inbox & Tasks",
        route = CheckerInboxTaskScreenRoute.toObjectNavigationRoute(),
        icon = MifosIcons.Checkbox,
    )

    data object CollectionSheetScreen : HomeDestinationsScreen(
        title = "Collection Sheet",
        route = IndividualCollectionSheetScreenRoute.toObjectNavigationRoute(),
        icon = MifosIcons.Assignment,
    )

    data object RunReportsScreen : HomeDestinationsScreen(
        title = "Run Reports",
        route = RunReportScreenRoute.toObjectNavigationRoute(),
        icon = MifosIcons.Task,
    )

    data object PathTrackerScreen : HomeDestinationsScreen(
        title = "Path Tracker",
        route = PathTrackingScreenRoute.toObjectNavigationRoute(),
        icon = MifosIcons.PersonPinCircle,
    )

    data object SettingsScreen : HomeDestinationsScreen(
        title = "Settings",
        route = SettingsRoute.toObjectNavigationRoute(),
        icon = MifosIcons.Settings,
    )

    data object AboutScreen : HomeDestinationsScreen(
        title = "About",
        route = AboutNavRoute.toObjectNavigationRoute(),
        icon = MifosIcons.Info,
    )

    data object OfflineSyncScreen : HomeDestinationsScreen(
        title = "Offline Sync",
        route = OfflineDashboardScreenRoute.toObjectNavigationRoute(),
        icon = MifosIcons.OfflineSync,
    )
}
