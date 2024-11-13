/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.offline.dashboard.OfflineDashboardRoute
import com.mifos.feature.offline.syncCenterPayloads.SyncCenterPayloadsScreenRoute
import com.mifos.feature.offline.syncClientPayloads.SyncClientPayloadsScreenRoute
import com.mifos.feature.offline.syncGroupPayloads.SyncGroupPayloadsScreenRoute
import com.mifos.feature.offline.syncLoanRepaymentTransaction.SyncLoanRepaymentTransactionScreenRoute
import com.mifos.feature.offline.syncSavingsAccountTransaction.SyncSavingsAccountTransactionScreenRoute

/**
 * Created by Pronay Sarker on 31/08/2024 (3:57 PM)
 */

fun NavGraphBuilder.offlineNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = OfflineScreens.OfflineDashboardScreens.route,
        route = "offline_graph",
    ) {
        offlineDashboardScreen(
            onBackPressed = navController::popBackStack,
            syncLoanRepayment = navController::navigateToSyncLoanRepaymentScreen,
            syncSavingsAccountTransactions = navController::navigateToSyncSavingsAccountTransactionsScreen,
            syncGroupPayload = navController::navigateToSyncGroupPayloadsScreen,
            syncClientPayload = navController::navigateToSyncClientPayloadsScreen,
            syncCenterPayload = navController::navigateToSyncCenterPayloadsScreen,
        )
        syncCenterPayloadsScreen(
            onBackPressed = navController::popBackStack,
        )
        syncGroupPayloadsScreen(
            onBackPressed = navController::popBackStack,
        )
        syncClientPayloadsScreen(
            onBackPressed = navController::popBackStack,
        )
        syncSavingsAccountTransactionsScreen(
            onBackPressed = navController::popBackStack,
        )
        syncLoanRepaymentScreen(
            onBackPressed = navController::popBackStack,
        )
    }
}

fun NavGraphBuilder.offlineDashboardScreen(
    onBackPressed: () -> Unit,
    syncSavingsAccountTransactions: () -> Unit,
    syncLoanRepayment: () -> Unit,
    syncGroupPayload: () -> Unit,
    syncClientPayload: () -> Unit,
    syncCenterPayload: () -> Unit,
) {
    composable(
        route = OfflineScreens.OfflineDashboardScreens.route,
    ) {
        OfflineDashboardRoute(
            onBackPressed = onBackPressed,
            syncSavingsAccountTransactions = syncSavingsAccountTransactions,
            syncLoanRepayment = syncLoanRepayment,
            syncGroupPayload = syncGroupPayload,
            syncCenterPayload = syncCenterPayload,
            syncClientPayload = syncClientPayload,
        )
    }
}

fun NavGraphBuilder.syncCenterPayloadsScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = OfflineScreens.SyncCenterPayloadsScreens.route,
    ) {
        SyncCenterPayloadsScreenRoute(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavGraphBuilder.syncGroupPayloadsScreen(
    onBackPressed: () -> Unit,
) {
    composable(OfflineScreens.SyncGroupPayloadsScreens.route) {
        SyncGroupPayloadsScreenRoute {
            onBackPressed()
        }
    }
}

fun NavGraphBuilder.syncClientPayloadsScreen(
    onBackPressed: () -> Unit,
) {
    composable(OfflineScreens.SyncClientPayloadsScreens.route) {
        SyncClientPayloadsScreenRoute(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavGraphBuilder.syncSavingsAccountTransactionsScreen(
    onBackPressed: () -> Unit,
) {
    composable(OfflineScreens.SyncSavingsAccountTransactionsScreens.route) {
        SyncSavingsAccountTransactionScreenRoute(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavGraphBuilder.syncLoanRepaymentScreen(
    onBackPressed: () -> Unit,
) {
    composable(route = OfflineScreens.SyncLoanRepaymentsScreens.route) {
        SyncLoanRepaymentTransactionScreenRoute {
            onBackPressed()
        }
    }
}

fun NavController.navigateToSyncCenterPayloadsScreen() {
    navigate(OfflineScreens.SyncCenterPayloadsScreens.route)
}

fun NavController.navigateToSyncGroupPayloadsScreen() {
    navigate(OfflineScreens.SyncGroupPayloadsScreens.route)
}

fun NavController.navigateToSyncClientPayloadsScreen() {
    navigate(OfflineScreens.SyncClientPayloadsScreens.route)
}

fun NavController.navigateToSyncSavingsAccountTransactionsScreen() {
    navigate(OfflineScreens.SyncSavingsAccountTransactionsScreens.route)
}

fun NavController.navigateToSyncLoanRepaymentScreen() {
    navigate(OfflineScreens.SyncLoanRepaymentsScreens.route)
}
