package com.mifos.mifosxdroid.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mifos.core.common.utils.Constants
import com.mifos.feature.about.navigation.aboutScreen
import com.mifos.feature.activate.navigation.activateScreen
import com.mifos.feature.activate.navigation.navigateToActivateScreen
import com.mifos.feature.center.navigation.centerNavGraph
import com.mifos.feature.checker_inbox_task.navigation.checkerInboxTaskGraph
import com.mifos.feature.client.navigation.clientNavGraph
import com.mifos.feature.document.navigation.documentListScreen
import com.mifos.feature.document.navigation.navigateToDocumentListScreen
import com.mifos.feature.groups.navigation.groupListScreen
import com.mifos.feature.individual_collection_sheet.navigation.generateCollectionSheetScreen
import com.mifos.feature.individual_collection_sheet.navigation.individualCollectionSheetNavGraph
import com.mifos.feature.loan.navigation.addLoanAccountScreen
import com.mifos.feature.loan.navigation.loanNavGraph
import com.mifos.feature.loan.navigation.navigateToLoanAccountScreen
import com.mifos.feature.loan.navigation.navigateToLoanAccountSummaryScreen
import com.mifos.feature.note.navigation.navigateToNoteScreen
import com.mifos.feature.note.navigation.noteScreen
import com.mifos.feature.path_tracking.navigation.pathTrackingScreen
import com.mifos.feature.report.navigation.reportNavGraph
import com.mifos.feature.savings.navigation.addSavingsAccountScreen
import com.mifos.feature.savings.navigation.navigateToAddSavingsAccount
import com.mifos.feature.savings.navigation.navigateToSavingsAccountSummaryScreen
import com.mifos.feature.savings.navigation.savingsNavGraph
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
            addLoanAccount = { navController.navigateToLoanAccountScreen(it) },
            addSavingsAccount = { navController.navigateToAddSavingsAccount(it, 0, false) },
            documents = { navController.navigateToDocumentListScreen(it, Constants.ENTITY_TYPE_CLIENTS) },
            moreClientInfo = {},
            notes = { navController.navigateToNoteScreen(it, Constants.ENTITY_TYPE_CLIENTS)},
            loanAccountSelected = { navController.navigateToLoanAccountSummaryScreen(it) },
            savingsAccountSelected = { id, type ->
                navController.navigateToSavingsAccountSummaryScreen(id, type)
            },
            activateClient = { navController.navigateToActivateScreen(it, Constants.ACTIVATE_CLIENT) }
        )

        savingsNavGraph(
            navController = navController,
            onBackPressed = navController::popBackStack,
            loadMoreSavingsAccountInfo = { },
            loadDocuments = { navController.navigateToDocumentListScreen(it, Constants.ENTITY_TYPE_SAVINGS) },
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

        activateScreen ( onBackPressed = navController::popBackStack )

        searchScreen(
            modifier = Modifier.padding(padding),
            centerListScreen = { },
            groupListScreen = { },
            clientListScreen = { }
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

        groupListScreen(
            paddingValues = padding,
            onAddGroupClick = {},
            onGroupClick = { group ->

            },
            onSyncClick = { groupLists ->

            }
        )

        checkerInboxTaskGraph(
            navController = navController
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

        individualCollectionSheetNavGraph(
            onBackPressed = { navController.popBackStack() }  ,
            navController = navController,
            navigateToPaymentDetails = {  _, _, _, _, _, _ ->
//                TODO() navigate to payment details
            }
        )
        generateCollectionSheetScreen ( onBackPressed = navController::popBackStack )
    }
}