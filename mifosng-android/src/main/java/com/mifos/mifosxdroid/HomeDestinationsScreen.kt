package com.mifos.mifosxdroid

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonPinCircle
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Task
import androidx.compose.ui.graphics.vector.ImageVector
import com.mifos.feature.about.navigation.ABOUT_SCREEN_ROUTE
import com.mifos.feature.center.navigation.CENTER_LIST_SCREEN_ROUTE
import com.mifos.feature.checker_inbox_task.navigation.CHECKER_INBOX_TASK_SCREEN_ROUTE
import com.mifos.feature.client.navigation.CLIENT_LIST_SCREEN_ROUTE
import com.mifos.feature.groups.navigation.GROUP_LIST_SCREEN_ROUTE
import com.mifos.feature.individual_collection_sheet.navigation.GENERATE_COLLECTION_SHEET_SCREEN_ROUTE
import com.mifos.feature.individual_collection_sheet.navigation.INDIVIDUAL_COLLECTION_SHEET_SCREEN_ROUTE
import com.mifos.feature.path_tracking.navigation.PATH_TRACKING_SCREEN_ROUTE
import com.mifos.feature.report.navigation.RUN_REPORTS_SCREEN_ROUTE
import com.mifos.feature.search.Navigation.SEARCH_SCREEN_ROUTE
import com.mifos.feature.settings.navigation.SETTINGS_SCREEN_ROUTE

sealed class HomeDestinationsScreen(
    val title: String = "",
    val route: String,
    val icon: ImageVector? = null,
) {
    data object SearchScreen : HomeDestinationsScreen(
        title = "Search",
        route = SEARCH_SCREEN_ROUTE,
        icon = Icons.Rounded.Dashboard
    )

    data object ClientListScreen : HomeDestinationsScreen(
        title = "Clients",
        route = CLIENT_LIST_SCREEN_ROUTE,
        icon = Icons.Rounded.Person
    )

    data object CenterListScreen : HomeDestinationsScreen(
        title = "Centers",
        route = CENTER_LIST_SCREEN_ROUTE,
        icon = Icons.Rounded.Business
    )

    data object GroupListScreen : HomeDestinationsScreen(
        title = "Groups",
        route = GROUP_LIST_SCREEN_ROUTE,
        icon = Icons.Rounded.Group
    )

    data object CheckerInboxAndTasksScreen : HomeDestinationsScreen(
        title = "Checker Inbox & Tasks",
        route = CHECKER_INBOX_TASK_SCREEN_ROUTE,
        icon = Icons.Rounded.CheckBox
    )

    data object IndividualCollectionSheetScreen : HomeDestinationsScreen(
        title = "Individual Collection Sheet",
        route = INDIVIDUAL_COLLECTION_SHEET_SCREEN_ROUTE,
        icon = Icons.AutoMirrored.Rounded.Assignment
    )

    data object CollectionSheetScreen : HomeDestinationsScreen(
        title = "Collection Sheet",
        route = GENERATE_COLLECTION_SHEET_SCREEN_ROUTE,
        icon = Icons.AutoMirrored.Rounded.Assignment
    )

    data object RunReportsScreen : HomeDestinationsScreen(
        title = "Run Reports",
        route = RUN_REPORTS_SCREEN_ROUTE,
        icon = Icons.Rounded.Task
    )

    data object PathTrackerScreen : HomeDestinationsScreen(
        title = "Path Tracker",
        route = PATH_TRACKING_SCREEN_ROUTE,
        icon = Icons.Rounded.PersonPinCircle
    )

    data object SettingsScreen : HomeDestinationsScreen(
        title = "Settings",
        route = SETTINGS_SCREEN_ROUTE,
        icon = Icons.Rounded.Settings
    )

    data object AboutScreen : HomeDestinationsScreen(
        title = "About",
        route = ABOUT_SCREEN_ROUTE,
        icon = Icons.Rounded.Info
    )

    data object OfflineSyncScreen : HomeDestinationsScreen(
        title = "Offline Sync",
        route = "offline_sync_screen",
        icon = Icons.Rounded.PowerSettingsNew
    )
}