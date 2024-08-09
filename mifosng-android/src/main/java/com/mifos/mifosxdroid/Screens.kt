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

sealed class Screens(
    val title: String = "",
    val route: String,
    val icon: ImageVector? = null,
) {

    data object SearchScreen : Screens(
        title = "Search",
        route = "search_screen",
        icon = Icons.Rounded.Dashboard
    )

    data object ClientListScreen : Screens(
        title = "Clients",
        route = "client_list_screen",
        icon = Icons.Rounded.Person
    )

    data object CenterListScreen : Screens(
        title = "Centers",
        route = "center_list_screen",
        icon = Icons.Rounded.Business
    )

    data object GroupListScreen : Screens(
        title = "Groups",
        route = "group_list_screen",
        icon = Icons.Rounded.Group
    )

    data object CheckerInboxAndTasksScreen : Screens(
        title = "Checker Inbox & Tasks",
        route = "checker_inbox_and_tasks_screen",
        icon = Icons.Rounded.CheckBox
    )

    data object IndividualCollectionSheetScreen : Screens(
        title = "Individual Collection Sheet",
        route = "individual_collection_sheet_screen",
        icon = Icons.AutoMirrored.Rounded.Assignment
    )

    data object CollectionSheetScreen : Screens(
        title = "Collection Sheet",
        route = "collection_sheet_screen",
        icon = Icons.AutoMirrored.Rounded.Assignment
    )

    data object RunReportsScreen : Screens(
        title = "Run Reports",
        route = "run_reports_screen",
        icon = Icons.Rounded.Task
    )

    data object PathTrackerScreen : Screens(
        title = "Path Tracker",
        route = "path_tracker_screen",
        icon = Icons.Rounded.PersonPinCircle
    )

    data object SettingsScreen : Screens(
        title = "Settings",
        route = "settings_screen",
        icon = Icons.Rounded.Settings
    )

    data object AboutScreen : Screens(
        title = "About",
        route = "about_screen",
        icon = Icons.Rounded.Info
    )

    data object OfflineSyncScreen : Screens(
        title = "Offline Sync",
        route = "offline_sync_screen",
        icon = Icons.Rounded.PowerSettingsNew
    )
}