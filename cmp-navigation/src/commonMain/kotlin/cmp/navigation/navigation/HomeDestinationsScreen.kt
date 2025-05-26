package cmp.navigation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.feature.about.navigation.AboutScreens

sealed class HomeDestinationsScreen(
    val title: String = "",
    val route: String,
    val icon: ImageVector? = null,
) {
    data object SearchScreen : HomeDestinationsScreen(
        title = "Search",
        route="search_screen",
        icon = MifosIcons.Dashboard,
    )

    data object ClientListScreen : HomeDestinationsScreen(
        title = "Clients",
        route = "client_list_screen",
        icon = MifosIcons.Person,
    )

    data object CenterListScreen : HomeDestinationsScreen(
        title = "Centers",
        route = "center_list_screen",
        icon = MifosIcons.Business,
    )

    data object GroupListScreen : HomeDestinationsScreen(
        title = "Groups",
        route="",
        icon = MifosIcons.Group,
    )

    data object CheckerInboxAndTasksScreen : HomeDestinationsScreen(
        title = "Checker Inbox & Tasks",
        route="",
        icon =MifosIcons.Checkbox ,
    )

    data object IndividualCollectionSheetScreen : HomeDestinationsScreen(
        title = "Individual Collection Sheet",
        route = "individual_collection_sheet_route",
        icon = MifosIcons.Assignment,
    )

    data object CollectionSheetScreen : HomeDestinationsScreen(
        title = "Collection Sheet",
        route = "generate_collection_sheet",
        icon = MifosIcons.Assignment,
    )

    data object RunReportsScreen : HomeDestinationsScreen(
        title = "Run Reports",
        route = "run_report_screen",
        icon = MifosIcons.Task,
    )

    data object PathTrackerScreen : HomeDestinationsScreen(
        title = "Path Tracker",
        route="",
        icon = MifosIcons.PersonPinCircle,
    )

    data object SettingsScreen : HomeDestinationsScreen(
        title = "Settings",
        route="",
        icon = MifosIcons.Settings,
    )

    data object AboutScreen : HomeDestinationsScreen(
        title = "About",
        route =AboutScreens.AboutScreen.route,
        icon = MifosIcons.Info,
    )

    data object OfflineSyncScreen : HomeDestinationsScreen(
        title = "Offline Sync",
        route="",
        icon = MifosIcons.PowerSettings,
    )
}
