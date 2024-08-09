package com.mifos.mifosxdroid.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mifos.feature.about.AboutScreen
import com.mifos.feature.center.center_list.ui.CenterListScreen
import com.mifos.feature.checker_inbox_task.checker_inbox_tasks.ui.CheckerInboxTasksScreen
import com.mifos.feature.client.clientList.presentation.ClientListScreen
import com.mifos.feature.groups.group_list.GroupsListRoute
import com.mifos.feature.individual_collection_sheet.generate_collection_sheet.GenerateCollectionSheetScreen
import com.mifos.feature.individual_collection_sheet.individual_collection_sheet.ui.IndividualCollectionSheetScreen
import com.mifos.feature.path_tracking.PathTrackingScreen
import com.mifos.feature.report.run_report.RunReportScreen
import com.mifos.feature.search.SearchScreenRoute
import com.mifos.feature.settings.settings.SettingsScreen
import com.mifos.mifosxdroid.Screens

@Composable
fun Navigation(navController: NavHostController, padding: PaddingValues) {

    NavHost(navController = navController, startDestination = Screens.SearchScreen.route) {
        composable(Screens.SearchScreen.route) {
            SearchScreenRoute(
                modifier = Modifier.padding(padding),
                onFabClick = {},
                onSearchOptionClick = {}
            )
        }
        composable(Screens.ClientListScreen.route) {
            ClientListScreen(
                paddingValues = padding,
                createNewClient = {},
                syncClicked = {},
                onClientSelect = {}
            )
        }
        composable(Screens.CenterListScreen.route) {
            CenterListScreen(
                paddingValues = padding,
                createNewCenter = {},
                syncClicked = {},
                onCenterSelect = {}
            )
        }
        composable(Screens.GroupListScreen.route) {
            GroupsListRoute(
                paddingValues = padding,
                onAddGroupClick = {},
                onGroupClick = {},
                onSyncClick = {}
            )
        }
        composable(Screens.CheckerInboxAndTasksScreen.route) {
            CheckerInboxTasksScreen(
                onBackPressed = {},
                checkerInbox = {}
            )
        }
        composable(Screens.IndividualCollectionSheetScreen.route) {
            IndividualCollectionSheetScreen(
                onBackPressed = {},
                onDetail = { String, IndividualCollectionSheet ->

                }
            )
        }
        composable(Screens.CollectionSheetScreen.route) {
            GenerateCollectionSheetScreen(
                onBackPressed = {}
            )
        }
        composable(Screens.RunReportsScreen.route) {
            RunReportScreen(
                onBackPressed = {},
                onReportClick = {}
            )
        }
        composable(Screens.PathTrackerScreen.route) {
            PathTrackingScreen(
                onBackPressed = {},
                onPathTrackingClick = {}
            )
        }
        composable(Screens.SettingsScreen.route) {
            SettingsScreen(
                onBackPressed = { },
                navigateToLoginScreen = { },
                changePasscode = {},
                languageChanged = { },
                serverConfig = {}
            )
        }
        composable(Screens.AboutScreen.route) {
            AboutScreen(
                onBackPressed = {}
            )
        }
        composable(Screens.OfflineSyncScreen.route) {

        }
    }

}