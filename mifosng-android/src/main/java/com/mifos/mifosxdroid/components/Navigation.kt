package com.mifos.mifosxdroid.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mifos.feature.about.navigation.aboutScreen
import com.mifos.feature.center.navigation.centerListScreen
import com.mifos.feature.center.navigation.navigateToCenterList
import com.mifos.feature.checker_inbox_task.navigation.checkerInboxTasksScreen
import com.mifos.feature.client.navigation.clientNavGraph
import com.mifos.feature.groups.navigation.groupListScreen
import com.mifos.feature.groups.navigation.navigateToGroupList
import com.mifos.feature.individual_collection_sheet.navigation.generateCollectionSheetScreen
import com.mifos.feature.individual_collection_sheet.navigation.individualCollectionSheetScreen
import com.mifos.feature.path_tracking.navigation.pathTrackingScreen
import com.mifos.feature.report.navigation.runReportsScreen
import com.mifos.feature.search.Navigation.SEARCH_SCREEN_ROUTE
import com.mifos.feature.search.Navigation.searchScreen
import com.mifos.feature.settings.navigation.settingsScreen

@Composable
fun Navigation(
    navController: NavHostController,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    startDestination: String = SEARCH_SCREEN_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        clientNavGraph(
            navController = navController,
            paddingValues = padding,
            addLoanAccount = {},
            addSavingsAccount = {},
            documents = {},
            moreClientInfo = {},
            notes = {},
            loanAccountSelected = {},
            savingsAccountSelected = { _, _ -> },
            activateClient = {}
        )

        searchScreen(
            modifier = Modifier.padding(padding),
            centerListScreen = { navController.navigateToCenterList() },
            groupListScreen = { navController.navigateToGroupList() },
            clientListScreen = { }
        )

        centerListScreen(
            paddingValues = padding,
            createNewCenter = {},
            syncClicked = {},
            onCenterSelect = {}
        )

        groupListScreen(
            paddingValues = padding,
            onAddGroupClick = {},
            onGroupClick = { group ->

            },
            onSyncClick = { groupLists ->

            }
        )

        checkerInboxTasksScreen(
            onBackPressed = { navController.popBackStack() },
        )

        individualCollectionSheetScreen(
            onBackClicked = { navController.popBackStack() },
            onDetail = { String, IndividualCollectionSheet ->

            }
        )

        generateCollectionSheetScreen(
            onBackPressed = { navController.popBackStack() }
        )

        runReportsScreen(
            onBackPressed = { navController.popBackStack() },
            onReportClick = { }
        )

        pathTrackingScreen(
            onBackPressed = { navController.popBackStack() }
        )

        settingsScreen(
            navigateBack = { navController.popBackStack() },
            navigateToLoginScreen = {},
            changePasscode = {},
            languageChanged = { },
            serverConfig = {}
        )

        aboutScreen(
            onBackPressed = { navController.popBackStack() }
        )

    }
}