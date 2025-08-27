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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import cmp.navigation.AppState
import com.mifos.core.common.utils.Constants
import com.mifos.feature.about.navigation.aboutNavGraph
import com.mifos.feature.activate.navigation.activateScreen
import com.mifos.feature.activate.navigation.navigateToActivateScreen
import com.mifos.feature.auth.navigation.navigateToLogin
import com.mifos.feature.center.navigation.centerNavGraph
import com.mifos.feature.center.navigation.navigateCenterDetailsScreenRoute
import com.mifos.feature.center.navigation.navigateCreateCenterScreenRoute
import com.mifos.feature.checker.inbox.task.navigation.checkerInboxTaskNavGraph
import com.mifos.feature.client.navigation.clientNavGraph
import com.mifos.feature.client.navigation.navigateClientDetailsScreen
import com.mifos.feature.client.navigation.navigateCreateClientScreen
import com.mifos.feature.client.navigation.navigateToClientListScreen
import com.mifos.feature.dataTable.navigation.dataTableNavGraph
import com.mifos.feature.dataTable.navigation.navigateDataTableList
import com.mifos.feature.dataTable.navigation.navigateToDataTable
import com.mifos.feature.document.navigation.documentListScreen
import com.mifos.feature.document.navigation.navigateToDocumentListScreen
import com.mifos.feature.groups.navigation.groupNavGraph
import com.mifos.feature.groups.navigation.navigateToCreateNewGroupScreen
import com.mifos.feature.groups.navigation.navigateToGroupDetailsScreen
import com.mifos.feature.individualCollectionSheet.navigation.generateCollectionSheetScreen
import com.mifos.feature.individualCollectionSheet.navigation.individualCollectionSheetNavGraph
import com.mifos.feature.loan.navigation.addLoanAccountScreen
import com.mifos.feature.loan.navigation.groupLoanScreen
import com.mifos.feature.loan.navigation.loanNavGraph
import com.mifos.feature.loan.navigation.navigateToGroupLoanScreen
import com.mifos.feature.loan.navigation.navigateToLoanAccountScreen
import com.mifos.feature.loan.navigation.navigateToLoanAccountSummaryScreen
import com.mifos.feature.note.navigation.navigateToNoteScreen
import com.mifos.feature.note.navigation.noteNavGraph
import com.mifos.feature.offline.navigation.offlineNavGraph
import com.mifos.feature.path.tracking.navigation.pathTrackingNavGraph
import com.mifos.feature.report.navigation.reportNavGraph
import com.mifos.feature.savings.navigation.navigateToAddSavingsAccount
import com.mifos.feature.savings.navigation.navigateToSavingsAccountSummaryScreen
import com.mifos.feature.savings.navigation.savingsNavGraph
import com.mifos.feature.search.navigation.searchNavGraph
import com.mifos.feature.settings.navigation.settingsScreen

@Composable
internal fun FeatureNavHost(
    appState: AppState,
    padding: PaddingValues,
    navigateToLogin: () -> Unit,
    onClickUpdateConfig: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        route = NavGraphRoute.MAIN_GRAPH,
        startDestination = HomeDestinationsScreen.SearchScreen.route,
        navController = appState.navController,
        modifier = modifier,
    ) {
        checkerInboxTaskNavGraph(appState.navController)

        documentListScreen(onBackPressed = appState.navController::popBackStack)

        dataTableNavGraph(
            navController = appState.navController,
            clientCreated = { client, userStatus ->
                appState.navController.popBackStack()

                if (!userStatus) {
                    client.id?.let { appState.navController.navigateClientDetailsScreen(it) }
                }
            },
        )

        searchNavGraph(
            paddingValues = padding,
            onCreateClient = appState.navController::navigateCreateClientScreen,
            onCreateCenter = appState.navController::navigateCreateCenterScreenRoute,
            onCreateGroup = appState.navController::navigateToCreateNewGroupScreen,
            onCenter = appState.navController::navigateCenterDetailsScreenRoute,
            onClient = appState.navController::navigateClientDetailsScreen,
            onGroup = appState.navController::navigateToGroupDetailsScreen,
            onLoan = appState.navController::navigateToLoanAccountSummaryScreen,
            onSavings = appState.navController::navigateClientDetailsScreen,
        )

        savingsNavGraph(
            navController = appState.navController,
            onBackPressed = appState.navController::popBackStack,
            loadMoreSavingsAccountInfo = appState.navController::navigateToDataTable,
            loadDocuments = appState.navController::navigateToDocumentListScreen,
        )

        aboutNavGraph(onBackPressed = appState.navController::popBackStack)

        offlineNavGraph(navController = appState.navController)

        noteNavGraph(
            navController = appState.navController,
            onBackPressed = appState.navController::popBackStack,
        )

        activateScreen(onBackPressed = appState.navController::popBackStack)

        centerNavGraph(
            navController = appState.navController,
            paddingValues = padding,
            onActivateCenter = appState.navController::navigateToActivateScreen,
            addSavingsAccount = { centerId ->
                appState.navController.navigateToAddSavingsAccount(0, centerId, false)
            },
        )

        groupNavGraph(
            navController = appState.navController,
            paddingValues = padding,
            addGroupLoanAccount = appState.navController::navigateToGroupLoanScreen,
            addSavingsAccount = appState.navController::navigateToAddSavingsAccount,
            loadDocumentList = appState.navController::navigateToDocumentListScreen,
            loadClientList = appState.navController::navigateToClientListScreen,
            loadSavingsAccountSummary = appState.navController::navigateToSavingsAccountSummaryScreen,
            loadGroupDataTables = appState.navController::navigateToDataTable,
            loadNotes = appState.navController::navigateToNoteScreen,
            loadLoanAccountSummary = appState.navController::navigateToLoanAccountSummaryScreen,
            activateGroup = appState.navController::navigateToActivateScreen,
        )

        settingsScreen(
            navigateBack = appState.navController::popBackStack,
            navigateToLoginScreen = navigateToLogin,
            changePasscode = {},
            onClickUpdateConfig = onClickUpdateConfig,
        )

        individualCollectionSheetNavGraph(
            navController = appState.navController,
            onBackPressed = appState.navController::popBackStack,
        )

        pathTrackingNavGraph(appState.navController)

        reportNavGraph(navController = appState.navController)

        loanNavGraph(
            navController = appState.navController,
            onMoreInfoClicked = appState.navController::navigateToDataTable,
            onDocumentsClicked = appState.navController::navigateToDocumentListScreen,
        )

        groupLoanScreen { appState.navController.popBackStack() }

        addLoanAccountScreen(
            onBackPressed = appState.navController::popBackStack,
            dataTable = { _, _ ->
//                navController.navigateDataTableList(dataTable, payload, Constants.CLIENT_LOAN)
//                TODO()
            },
        )

        generateCollectionSheetScreen(appState.navController::popBackStack)

        // TODO : check appState.navController::navigateDataTableList after completing client creation
        clientNavGraph(
            navController = appState.navController,
            paddingValues = padding,
            addLoanAccount = appState.navController::navigateToLoanAccountScreen,
            addSavingsAccount = { clientId ->
                appState.navController.navigateToAddSavingsAccount(0, clientId, false)
            },
            documents = { clientId ->
                appState.navController.navigateToDocumentListScreen(
                    clientId,
                    Constants.ENTITY_TYPE_CLIENTS,
                )
            },
            moreClientInfo = { clientId ->
                appState.navController.navigateToDataTable(
                    Constants.DATA_TABLE_NAME_CLIENT,
                    clientId,
                )
            },
            notes = { clientId ->
                appState.navController.navigateToNoteScreen(
                    clientId,
                    Constants.ENTITY_TYPE_CLIENTS,
                )
            },
            loanAccountSelected = { loanAccountNumber ->
                appState.navController.navigateToLoanAccountSummaryScreen(loanAccountNumber)
            },
            savingsAccountSelected = { clientId, depositType ->
                appState.navController.navigateToSavingsAccountSummaryScreen(clientId, depositType)
            },
            activateClient = { clientId ->
                appState.navController.navigateToActivateScreen(
                    clientId,
                    Constants.ACTIVATE_CLIENT,
                )
            },
            hasDatatables = appState.navController::navigateDataTableList,
            onDocumentClicked = appState.navController::navigateToDocumentListScreen,
            navigateToHome = {
                appState.navController.navigateAndClearBackStack(HomeDestinationsScreen.SearchScreen.route)
            },
        )
    }
}

// id showing error in ide eventhough syntax correct and app is building fine so don't pay attention to id red colour
fun NavController.navigateAndClearBackStack(route: String) {
    this.navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
        restoreState = false
    }
}
