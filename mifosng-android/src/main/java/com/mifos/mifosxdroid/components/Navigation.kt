package com.mifos.mifosxdroid.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mifos.feature.about.navigation.aboutScreen
import com.mifos.feature.center.navigation.centerNavGraph
import com.mifos.feature.checker_inbox_task.navigation.checkerInboxTasksScreen
import com.mifos.feature.client.navigation.clientNavGraph
import com.mifos.feature.groups.navigation.groupListScreen
import com.mifos.feature.individual_collection_sheet.navigation.generateCollectionSheetScreen
import com.mifos.feature.individual_collection_sheet.navigation.individualCollectionSheetScreen
import com.mifos.feature.path_tracking.navigation.pathTrackingScreen
import com.mifos.feature.report.navigation.reportNavGraph
import com.mifos.feature.savings.navigation.addSavingsAccountScreen
import com.mifos.feature.savings.navigation.navigateToAddSavingsAccount
import com.mifos.feature.savings.navigation.navigateToSavingsAccountSummaryScreen
import com.mifos.feature.savings.navigation.savingsSummaryNavGraph
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
            addSavingsAccount = { navController.navigateToAddSavingsAccount(it, 0, false) },
            documents = {},
            moreClientInfo = {},
            notes = {},
            loanAccountSelected = {},
            savingsAccountSelected = { id, type ->
                navController.navigateToSavingsAccountSummaryScreen(id, type)
            },
            activateClient = { }
        )

        savingsSummaryNavGraph(
            navController = navController,
            onBackPressed = navController::popBackStack,
            loadMoreSavingsAccountInfo = { },
            loadDocuments = { },
        )

        addSavingsAccountScreen(
            onBackPressed = navController::popBackStack
        )

        searchScreen(
            modifier = Modifier.padding(padding),
            centerListScreen = { },
            groupListScreen = { },
            clientListScreen = { }
        )

        centerNavGraph(
            navController = navController,
            paddingValues = padding,
            onActivateCenter = { _, _ -> },
            addSavingsAccount = { }
        )

        reportNavGraph(
            navController = navController
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