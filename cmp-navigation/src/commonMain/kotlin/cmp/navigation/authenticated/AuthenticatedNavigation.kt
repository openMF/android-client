/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation.authenticated

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.mifos.feature.about.aboutDestination
import com.mifos.feature.activate.activateDestination
import com.mifos.feature.checker.inbox.task.navigation.checkerInboxTaskNavGraph
import com.mifos.feature.client.navigation.navigateClientDetailsScreen
import com.mifos.feature.dataTable.navigation.dataTableNavGraph
import com.mifos.feature.dataTable.navigation.navigateToDataTable
import com.mifos.feature.document.navigation.documentListScreen
import com.mifos.feature.document.navigation.navigateToDocumentListScreen
import com.mifos.feature.individualCollectionSheet.navigation.generateCollectionSheetScreen
import com.mifos.feature.individualCollectionSheet.navigation.individualCollectionSheetNavGraph
import com.mifos.feature.loan.groupLoanAccount.groupLoanScreen
import com.mifos.feature.loan.loanAccount.addLoanAccountScreen
import com.mifos.feature.loan.navigation.loanNavGraph
import com.mifos.feature.loan.newLoanAccount.navigateToNewLoanAccountRoute
import com.mifos.feature.note.navigation.noteNavGraph
import com.mifos.feature.note.notes.navigateToNoteScreen
import com.mifos.feature.offline.navigation.offlineNavGraph
import com.mifos.feature.path.tracking.navigation.pathTrackingRoute
import com.mifos.feature.report.navigation.reportNavGraph
import com.mifos.feature.savings.navigation.savingsNavGraph
import com.mifos.feature.savings.savingsAccountv2.navigateToSavingsAccountRoute
import com.mifos.feature.settings.navigation.settingsScreen
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
internal data object AuthenticatedGraph

internal fun NavController.navigateToAuthenticatedGraph(navOptions: NavOptions? = null) {
    navigate(route = AuthenticatedGraph, navOptions = navOptions)
}

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
internal fun NavGraphBuilder.authenticatedGraph(
    navController: NavController,
) {
    navigation<AuthenticatedGraph>(
        startDestination = AuthenticatedNavbar,
    ) {
        authenticatedNavbarGraph(
            onDrawerItemClick = {
                navController.navigate(it) {
                    launchSingleTop = true
                }
            },
            navigateToDocumentScreen = navController::navigateToDocumentListScreen,
            navigateToNoteScreen = navController::navigateToNoteScreen,
            navigateToNewLoanAccountScreen = navController::navigateToNewLoanAccountRoute,
            navigateToNewSavingsAccountScreen = navController::navigateToSavingsAccountRoute,
        )

        checkerInboxTaskNavGraph(navController)

        documentListScreen(onBackPressed = navController::popBackStack)

        dataTableNavGraph(
            navController = navController,
            clientCreated = { client, userStatus ->
                navController.popBackStack()

                if (!userStatus) {
                    client.id?.let { navController.navigateClientDetailsScreen(it) }
                }
            },
        )

        savingsNavGraph(
            navController = navController,
            onBackPressed = navController::popBackStack,
            loadMoreSavingsAccountInfo = navController::navigateToDataTable,
            loadDocuments = navController::navigateToDocumentListScreen,
        )

        aboutDestination(onBackPressed = navController::popBackStack)

        offlineNavGraph(navController = navController)

        noteNavGraph(
            navController = navController,
            onBackPressed = navController::popBackStack,
        )

        activateDestination(onBackPressed = navController::popBackStack)

        settingsScreen(
            navigateBack = navController::popBackStack,
            navigateToLoginScreen = {},
            changePasscode = {},
            onClickUpdateConfig = {},
        )

        individualCollectionSheetNavGraph(
            navController = navController,
            onBackPressed = navController::popBackStack,
        )

        pathTrackingRoute(navController::popBackStack)

        reportNavGraph(navController = navController)

        loanNavGraph(
            navController = navController,
            onMoreInfoClicked = navController::navigateToDataTable,
            onDocumentsClicked = navController::navigateToDocumentListScreen,
        )

        groupLoanScreen { navController.popBackStack() }

        addLoanAccountScreen(
            onBackPressed = navController::popBackStack,
            dataTable = { _, _ ->
//                navController.navigateDataTableList(dataTable, payload, Constants.CLIENT_LOAN)
//                TODO()
            },
        )

        generateCollectionSheetScreen(navController::popBackStack)
    }
}
