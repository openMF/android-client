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
import com.mifos.feature.checker_inbox_task.navigation.CHECKER_INBOX_TASK_SCREEN_ROUTE
import com.mifos.feature.groups.navigation.GROUP_LIST_SCREEN_ROUTE
import com.mifos.feature.path_tracking.navigation.PATH_TRACKING_SCREEN_ROUTE
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
        route = "client_list_screen",
        icon = Icons.Rounded.Person
    )

    data object CenterListScreen : HomeDestinationsScreen(
        title = "Centers",
        route = "center_list_screen",
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
        route = "individual_collection_sheet_route",
        icon = Icons.AutoMirrored.Rounded.Assignment
    )

    data object CollectionSheetScreen : HomeDestinationsScreen(
        title = "Collection Sheet",
        route = "generate_collection_sheet",
        icon = Icons.AutoMirrored.Rounded.Assignment
    )

    data object RunReportsScreen : HomeDestinationsScreen(
        title = "Run Reports",
        route = "run_report_screen",
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
        route = "about_screen_route",
        icon = Icons.Rounded.Info
    )

    data object OfflineSyncScreen : HomeDestinationsScreen(
        title = "Offline Sync",
        route = "offline_sync_screen",
        icon = Icons.Rounded.PowerSettingsNew
    )
}