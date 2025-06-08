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
import com.mifos.feature.center.navigation.centerNavGraph
import com.mifos.feature.checker.inbox.task.navigation.checkerInboxTaskNavGraph
import com.mifos.feature.note.navigation.noteNavGraph
import com.mifos.feature.pathTracking.navigation.pathTrackingNavGraph
import com.mifos.feature.search.navigation.searchNavGraph
import com.mifos.feature.settings.navigation.settingsScreen

@Composable
internal fun FeatureNavHost(
    appState: AppState,
    onClickLogout: () -> Unit,
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
            onCreateCenter = { println("Create Center") },
            onCreateGroup = { println("Create Group") },
            onClient = { id -> println("Client clicked: $id") },
            onCenter = { id -> println("Center clicked: $id") },
            onGroup = { id -> println("Group clicked: $id") },
            onLoan = { id -> println("Loan clicked: $id") },
            onSavings = { id -> println("Savings clicked: $id") },
        )

        aboutNavGraph(onBackPressed = appState.navController::popBackStack)

        noteNavGraph(onBackPressed = appState.navController::popBackStack)

        activateScreen(onBackPressed = appState.navController::popBackStack)

        centerNavGraph(
            navController = appState.navController,
            paddingValues = padding,
            onActivateCenter = appState.navController::navigateToActivateScreen,
            addSavingsAccount = {
//                navController.navigateToAddSavingsAccount(it, 0, true)
            },

        )

        settingsScreen(
            navigateBack = appState.navController::popBackStack,
            navigateToLoginScreen = {},
            changePasscode = {},
            languageChanged = {},
        )

        pathTrackingNavGraph(appState.navController)
    }
}
