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
import com.mifos.feature.checker.inbox.task.navigation.CheckerInboxTaskScreenRoute
import com.mifos.feature.individualCollectionSheet.navigation.IndividualCollectionSheetScreenRoute
import com.mifos.feature.offline.navigation.OfflineDashboardScreenRoute
import com.mifos.feature.path.tracking.navigation.PathTrackingScreenRoute
import com.mifos.feature.report.navigation.RunReportScreenRoute
import com.mifos.feature.settings.navigation.SettingsRoute
import org.jetbrains.compose.resources.StringResource
import org.mifos.navigation.generated.resources.Res
import org.mifos.navigation.generated.resources.about
import org.mifos.navigation.generated.resources.checker_inbox_tasks
import org.mifos.navigation.generated.resources.collection_sheet
import org.mifos.navigation.generated.resources.offline_sync
import org.mifos.navigation.generated.resources.path_tracker
import org.mifos.navigation.generated.resources.run_reports
import org.mifos.navigation.generated.resources.settings

sealed class HomeDestinationsScreen(
    val title: StringResource,
    val route: String,
    val icon: ImageVector? = null,
) {
    data object CheckerInboxAndTasksScreen : HomeDestinationsScreen(
        title = Res.string.checker_inbox_tasks,
        route = CheckerInboxTaskScreenRoute.toObjectNavigationRoute(),
        icon = MifosIcons.Checkbox,
    )

    data object CollectionSheetScreen : HomeDestinationsScreen(
        title = Res.string.collection_sheet,
        route = IndividualCollectionSheetScreenRoute.toObjectNavigationRoute(),
        icon = MifosIcons.Assignment,
    )

    data object RunReportsScreen : HomeDestinationsScreen(
        title = Res.string.run_reports,
        route = RunReportScreenRoute.toObjectNavigationRoute(),
        icon = MifosIcons.Task,
    )

    data object PathTrackerScreen : HomeDestinationsScreen(
        title = Res.string.path_tracker,
        route = PathTrackingScreenRoute.toObjectNavigationRoute(),
        icon = MifosIcons.PersonPinCircle,
    )

    data object SettingsScreen : HomeDestinationsScreen(
        title = Res.string.settings,
        route = SettingsRoute.toObjectNavigationRoute(),
        icon = MifosIcons.Settings,
    )

    data object AboutScreen : HomeDestinationsScreen(
        title = Res.string.about,
        route = AboutNavRoute.toObjectNavigationRoute(),
        icon = MifosIcons.Info,
    )

    data object OfflineSyncScreen : HomeDestinationsScreen(
        title = Res.string.offline_sync,
        route = OfflineDashboardScreenRoute.toObjectNavigationRoute(),
        icon = MifosIcons.OfflineSync,
    )
}
