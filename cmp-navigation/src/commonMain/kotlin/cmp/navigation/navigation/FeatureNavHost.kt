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
import androidx.navigation.compose.NavHost
import cmp.navigation.AppState
import com.mifos.feature.about.navigation.aboutNavGraph
import com.mifos.feature.activate.navigation.activateScreen
import com.mifos.feature.activate.navigation.navigateToActivateScreen
import com.mifos.feature.auth.navigation.navigateToLogin
import com.mifos.feature.center.navigation.centerNavGraph
import com.mifos.feature.center.navigation.navigateCreateCenterScreenRoute
import com.mifos.feature.checker.inbox.task.navigation.checkerInboxTaskNavGraph
import com.mifos.feature.groups.navigation.groupNavGraph
import com.mifos.feature.groups.navigation.navigateToCreateNewGroupScreen
import com.mifos.feature.individualCollectionSheet.navigation.individualCollectionSheetNavGraph
import com.mifos.feature.note.navigation.navigateToNoteScreen
import com.mifos.feature.note.navigation.noteNavGraph
import com.mifos.feature.offline.navigation.offlineNavGraph
import com.mifos.feature.pathTracking.navigation.pathTrackingNavGraph
import com.mifos.feature.savings.navigation.navigateToAddSavingsAccount
import com.mifos.feature.savings.navigation.navigateToSavingsAccountSummaryScreen
import com.mifos.feature.savings.navigation.savingsNavGraph
import com.mifos.feature.search.navigation.searchNavGraph
import com.mifos.feature.settings.navigation.settingsScreen

@Composable
internal fun FeatureNavHost(
    appState: AppState,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    NavHost(
        route = NavGraphRoute.MAIN_GRAPH,
        startDestination = HomeDestinationsScreen.SearchScreen.route,
        navController = appState.navController,
        modifier = modifier,
    ) {
        checkerInboxTaskNavGraph(appState.navController)

        searchNavGraph(
            paddingValues = padding,
            onCreateClient = { println("Create Client") },
            onCreateCenter = appState.navController::navigateCreateCenterScreenRoute,
            onCreateGroup = appState.navController::navigateToCreateNewGroupScreen,
            onClient = { id -> println("Client clicked: $id") },
            onCenter = { id -> println("Center clicked: $id") },
            onGroup = { id -> println("Group clicked: $id") },
            onLoan = { id -> println("Loan clicked: $id") },
            onSavings = { id -> println("Savings clicked: $id") },
        )

        savingsNavGraph(
            navController = appState.navController,
            onBackPressed = appState.navController::popBackStack,
            loadDocuments = { _, _ -> },
            loadMoreSavingsAccountInfo = { _, _ -> },
        )

        aboutNavGraph(onBackPressed = appState.navController::popBackStack)

        offlineNavGraph(navController = appState.navController)

        noteNavGraph(onBackPressed = appState.navController::popBackStack)

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
            addGroupLoanAccount = {},
            addSavingsAccount = appState.navController::navigateToAddSavingsAccount,
            loadDocumentList = { _, _ -> },
            clientListFragment = {},
            loadSavingsAccountSummary = appState.navController::navigateToSavingsAccountSummaryScreen,
            loadGroupDataTables = { _, _ -> },
            loadNotes = appState.navController::navigateToNoteScreen,
            loadLoanAccountSummary = { _ -> },
            activateGroup = appState.navController::navigateToActivateScreen,
        )

        settingsScreen(
            navigateBack = appState.navController::popBackStack,
            navigateToLoginScreen = appState.navController::navigateToLogin,
            changePasscode = {},
            languageChanged = {},
        )

        individualCollectionSheetNavGraph(
            navController = appState.navController,
            onBackPressed = appState.navController::popBackStack,
        )

        pathTrackingNavGraph(appState.navController)
    }
}
