package com.mifos.mifosxdroid.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mifos.core.common.utils.Constants
import com.mifos.feature.about.navigation.aboutScreen
import com.mifos.feature.activate.navigation.activateScreen
import com.mifos.feature.activate.navigation.navigateToActivateScreen
import com.mifos.feature.center.navigation.centerNavGraph
import com.mifos.feature.center.navigation.navigateCenterDetailsScreenRoute
import com.mifos.feature.center.navigation.navigateCreateCenterScreenRoute
import com.mifos.feature.checker_inbox_task.navigation.checkerInboxTaskGraph
import com.mifos.feature.client.navigation.clientNavGraph
import com.mifos.feature.client.navigation.navigateClientDetailsScreen
import com.mifos.feature.client.navigation.navigateClientSurveyListScreen
import com.mifos.feature.client.navigation.navigateCreateClientScreen

import com.mifos.feature.data_table.navigation.dataTableNavGraph
import com.mifos.feature.data_table.navigation.navigateDataTable


import com.mifos.feature.document.navigation.documentListScreen
import com.mifos.feature.document.navigation.navigateToDocumentListScreen
import com.mifos.feature.groups.navigation.groupListScreenRoute
import com.mifos.feature.groups.navigation.groupNavGraph
import com.mifos.feature.groups.navigation.navigateToCreateNewGroupScreen
import com.mifos.feature.individual_collection_sheet.navigation.generateCollectionSheetScreen
import com.mifos.feature.individual_collection_sheet.navigation.individualCollectionSheetNavGraph
import com.mifos.feature.loan.group_loan_account.GroupLoanAccountScreen
import com.mifos.feature.loan.navigation.addLoanAccountScreen
import com.mifos.feature.loan.navigation.groupLoanScreen
import com.mifos.feature.loan.navigation.loanNavGraph
import com.mifos.feature.loan.navigation.navigateToGroupLoanScreen
import com.mifos.feature.loan.navigation.navigateToLoanAccountScreen
import com.mifos.feature.loan.navigation.navigateToLoanAccountSummaryScreen
import com.mifos.feature.note.navigation.navigateToNoteScreen
import com.mifos.feature.note.navigation.noteScreen
import com.mifos.feature.path_tracking.navigation.pathTrackingNavGraph
import com.mifos.feature.report.navigation.reportNavGraph
import com.mifos.feature.savings.navigation.addSavingsAccountScreen
import com.mifos.feature.savings.navigation.navigateToAddSavingsAccount
import com.mifos.feature.savings.navigation.navigateToSavingsAccountSummaryScreen
import com.mifos.feature.savings.navigation.savingsNavGraph
import com.mifos.feature.search.navigation.SearchScreens
import com.mifos.feature.search.navigation.searchNavGraph
import com.mifos.feature.settings.navigation.settingsScreen

@Composable
fun Navigation(
    navController: NavHostController,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    startDestination: String = SearchScreens.SearchScreenRoute.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        clientNavGraph(
            navController = navController,
            paddingValues = padding,
            addLoanAccount = { navController.navigateToLoanAccountScreen(it) },
            addSavingsAccount = { navController.navigateToAddSavingsAccount(0, it, false) },
            documents = {
                navController.navigateToDocumentListScreen(
                    it,
                    Constants.ENTITY_TYPE_CLIENTS
                )
            },
            moreClientInfo = {
                navController.navigateDataTable(
                    Constants.DATA_TABLE_NAME_CLIENT,
                    it
                )
            },
            notes = { navController.navigateToNoteScreen(it, Constants.ENTITY_TYPE_CLIENTS) },
            loanAccountSelected = { navController.navigateToLoanAccountSummaryScreen(it) },
            savingsAccountSelected = { id, type ->
                navController.navigateToSavingsAccountSummaryScreen(id, type)
            },
            activateClient = {
                navController.navigateToActivateScreen(
                    it,
                    Constants.ACTIVATE_CLIENT
                )
            }
        )
        
        groupNavGraph(
            paddingValues = padding,
            navController = navController,
            addGroupLoanAccount = navController::navigateToGroupLoanScreen,
            addSavingsAccount = { navController.navigateToAddSavingsAccount(it, 0, true) },
            loadDocumentList = { navController.navigateToDocumentListScreen(it, Constants.ENTITY_TYPE_GROUPS)},
            clientListFragment = { TODO() },
            loadGroupDataTables = { TODO() },
            loadNotes = { navController.navigateToNoteScreen(it, Constants.ENTITY_TYPE_GROUPS)},
            loadLoanAccountSummary = navController::navigateToLoanAccountSummaryScreen,
            loadSavingsAccountSummary = navController::navigateToSavingsAccountSummaryScreen,
            activateGroup = { navController.navigateToActivateScreen(it, Constants.ACTIVATE_GROUP)}
        )

        groupLoanScreen { navController.popBackStack() }

        savingsNavGraph(
            navController = navController,
            onBackPressed = navController::popBackStack,
            loadMoreSavingsAccountInfo = { },
            loadDocuments = {
                navController.navigateToDocumentListScreen(
                    it,
                    Constants.ENTITY_TYPE_SAVINGS
                )
            },
        )

        loanNavGraph(
            navController = navController,
            onMoreInfoClicked = { },
            onDocumentsClicked = navController::navigateToDocumentListScreen
        )

        documentListScreen(
            onBackPressed = navController::popBackStack
        )

        noteScreen(
            onBackPressed = navController::popBackStack
        )

        addLoanAccountScreen(
            onBackPressed = navController::popBackStack,
            dataTable = { _, _ -> }
        )

        addSavingsAccountScreen(
            onBackPressed = navController::popBackStack
        )

        activateScreen(onBackPressed = navController::popBackStack)

        searchNavGraph(
            paddingValues = padding,
            onCreateCenter = navController::navigateCreateCenterScreenRoute,
            onCreateClient = navController::navigateCreateClientScreen,
            onCreateGroup = {},
            onCenter = navController::navigateCenterDetailsScreenRoute,
            onClient = navController::navigateClientDetailsScreen,
            onLoan = {},
            onGroup = {},
            onSavings = {}
        )

        centerNavGraph(
            navController = navController,
            paddingValues = padding,
            onActivateCenter = navController::navigateToActivateScreen,
            addSavingsAccount = {
//                navController.navigateToAddSavingsAccount(0, it, true)
            }
        )

        reportNavGraph(
            navController = navController
        )

        checkerInboxTaskGraph(
            navController = navController
        )

        pathTrackingNavGraph(
            navController = navController
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

        individualCollectionSheetNavGraph(
            onBackPressed = { navController.popBackStack() },
            navController = navController,
            navigateToPaymentDetails = { _, _, _, _, _, _ ->
//                TODO() navigate to payment details
            }
        )
        generateCollectionSheetScreen(onBackPressed = navController::popBackStack)


        dataTableNavGraph(
            navController = navController,
            clientCreated = {}
        )


    }
}