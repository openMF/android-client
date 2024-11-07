package com.mifos.mifosxdroid.components

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mifos.core.common.utils.Constants
import com.mifos.feature.about.navigation.aboutScreen
import com.mifos.feature.activate.navigation.activateScreen
import com.mifos.feature.activate.navigation.navigateToActivateScreen
import com.mifos.feature.center.navigation.centerNavGraph
import com.mifos.feature.center.navigation.navigateCenterDetailsScreenRoute
import com.mifos.feature.center.navigation.navigateCreateCenterScreenRoute
import com.mifos.feature.center.navigation.navigateSyncCentersDialog
import com.mifos.feature.checker_inbox_task.navigation.checkerInboxTaskGraph
import com.mifos.feature.client.navigation.clientNavGraph
import com.mifos.feature.client.navigation.navigateClientDetailsScreen
import com.mifos.feature.client.navigation.navigateCreateClientScreen
import com.mifos.feature.client.navigation.navigateToClientListScreen

import com.mifos.feature.data_table.navigation.dataTableNavGraph
import com.mifos.feature.data_table.navigation.navigateToDataTable


import com.mifos.feature.document.navigation.documentListScreen
import com.mifos.feature.document.navigation.navigateToDocumentListScreen
import com.mifos.feature.groups.navigation.groupNavGraph
import com.mifos.feature.groups.navigation.navigateToCreateNewGroupScreen
import com.mifos.feature.groups.navigation.navigateToGroupDetailsScreen
import com.mifos.feature.individual_collection_sheet.navigation.generateCollectionSheetScreen
import com.mifos.feature.individual_collection_sheet.navigation.individualCollectionSheetNavGraph
import com.mifos.feature.loan.navigation.addLoanAccountScreen
import com.mifos.feature.loan.navigation.groupLoanScreen
import com.mifos.feature.loan.navigation.loanNavGraph
import com.mifos.feature.loan.navigation.navigateToGroupLoanScreen
import com.mifos.feature.loan.navigation.navigateToLoanAccountScreen
import com.mifos.feature.loan.navigation.navigateToLoanAccountSummaryScreen
import com.mifos.feature.note.navigation.navigateToNoteScreen
import com.mifos.feature.note.navigation.noteScreen
import com.mifos.feature.offline.navigation.navigateToSyncCenterPayloadsScreen
import com.mifos.feature.offline.navigation.offlineNavGraph
import com.mifos.feature.path_tracking.navigation.pathTrackingNavGraph
import com.mifos.feature.report.navigation.reportNavGraph
import com.mifos.feature.savings.navigation.addSavingsAccountScreen
import com.mifos.feature.savings.navigation.navigateToAddSavingsAccount
import com.mifos.feature.savings.navigation.navigateToSavingsAccountSummaryScreen
import com.mifos.feature.savings.navigation.savingsNavGraph
import com.mifos.feature.search.navigation.SearchScreens
import com.mifos.feature.search.navigation.searchNavGraph
import com.mifos.feature.settings.navigation.settingsScreen
import com.mifos.mifosxdroid.R
import com.mifos.utils.MifosResponseHandler

@Composable
fun Navigation(
    navController: NavHostController,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    startDestination: String = SearchScreens.SearchScreenRoute.route
) {
    val context = LocalContext.current

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
                navController.navigateToDataTable(
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
            addSavingsAccount = navController::navigateToAddSavingsAccount,
            loadDocumentList = navController::navigateToDocumentListScreen,
            clientListFragment = { _ -> navController.navigateToClientListScreen() },
            loadGroupDataTables = navController::navigateToDataTable,
            loadNotes = navController::navigateToNoteScreen,
            loadLoanAccountSummary = navController::navigateToLoanAccountSummaryScreen,
            loadSavingsAccountSummary = navController::navigateToSavingsAccountSummaryScreen,
            activateGroup = navController::navigateToActivateScreen
        )

        groupLoanScreen { navController.popBackStack() }

        savingsNavGraph(
            navController = navController,
            onBackPressed = navController::popBackStack,
            loadMoreSavingsAccountInfo = { navController.navigateToDataTable(Constants.DATA_TABLE_NAME_SAVINGS, it) },
            loadDocuments = {
                navController.navigateToDocumentListScreen(
                    it,
                    Constants.ENTITY_TYPE_SAVINGS
                )
            },
        )

        loanNavGraph(
            navController = navController,
            onMoreInfoClicked = navController::navigateToDataTable,
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
            dataTable = { dataTable, payload ->
//                navController.navigateDataTableList(dataTable, payload, Constants.CLIENT_LOAN)
//                TODO()
            }
        )

        addSavingsAccountScreen(
            onBackPressed = navController::popBackStack
        )

        activateScreen(onBackPressed = navController::popBackStack)

        searchNavGraph(
            paddingValues = padding,
            onCreateCenter = navController::navigateCreateCenterScreenRoute,
            onCreateClient = navController::navigateCreateClientScreen,
            onCreateGroup = navController::navigateToCreateNewGroupScreen,
            onCenter = navController::navigateCenterDetailsScreenRoute,
            onClient = navController::navigateClientDetailsScreen,
            onLoan = navController::navigateToLoanAccountSummaryScreen,
            onGroup = navController::navigateToGroupDetailsScreen,
            onSavings = navController::navigateClientDetailsScreen
        )

        centerNavGraph(
            navController = navController,
            paddingValues = padding,
            onActivateCenter = navController::navigateToActivateScreen,
            addSavingsAccount = {
//                TODO() check this logic
                navController.navigateToAddSavingsAccount(it, 0, true)
            },

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
            navigateBack = navController::popBackStack,
            navigateToLoginScreen = {},
            changePasscode = { },
            languageChanged = { },
        )

        aboutScreen(
            onBackPressed = { navController.popBackStack() }
        )

        individualCollectionSheetNavGraph(
            onBackPressed = { navController.popBackStack() },
            navController = navController,
        )

        generateCollectionSheetScreen(onBackPressed = navController::popBackStack)

        dataTableNavGraph(
            navController = navController,
            clientCreated = { client , userStatus ->
                navController.popBackStack()
                navController.popBackStack()
                Toast.makeText(context, context.resources.getString(R.string.client) + MifosResponseHandler.response, Toast.LENGTH_LONG).show()

                if(userStatus == Constants.USER_ONLINE){
                    client.clientId?.let { navController.navigateClientDetailsScreen(it) }
                }
            }
        )

        offlineNavGraph(
            navController = navController
        )
    }
}