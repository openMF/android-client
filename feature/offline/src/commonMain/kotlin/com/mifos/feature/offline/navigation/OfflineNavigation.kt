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
import kotlinx.serialization.Serializable

@Serializable
data object OfflineNavGraph

fun NavGraphBuilder.offlineNavGraph(
    navController: NavController,
) {
    navigation<OfflineNavGraph>(
        startDestination = OfflineDashboardScreenRoute,
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

@Serializable
data object OfflineDashboardScreenRoute

fun NavGraphBuilder.offlineDashboardScreen(
    onBackPressed: () -> Unit,
    syncSavingsAccountTransactions: () -> Unit,
    syncLoanRepayment: () -> Unit,
    syncGroupPayload: () -> Unit,
    syncClientPayload: () -> Unit,
    syncCenterPayload: () -> Unit,
) {
    composable<OfflineDashboardScreenRoute> {
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
    composable<SyncCenterPayloadsScreenRoute> {
        SyncCenterPayloadsScreenRoute(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavGraphBuilder.syncGroupPayloadsScreen(
    onBackPressed: () -> Unit,
) {
    composable<SyncGroupPayloadsScreenRoute> {
        SyncGroupPayloadsScreenRoute {
            onBackPressed()
        }
    }
}

fun NavGraphBuilder.syncClientPayloadsScreen(
    onBackPressed: () -> Unit,
) {
    composable<SyncClientPayloadsScreenRoute> {
        SyncClientPayloadsScreenRoute(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavGraphBuilder.syncSavingsAccountTransactionsScreen(
    onBackPressed: () -> Unit,
) {
    composable<SyncSavingsAccountTransactionsScreenRoute> {
        SyncSavingsAccountTransactionScreenRoute(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavGraphBuilder.syncLoanRepaymentScreen(
    onBackPressed: () -> Unit,
) {
    composable<SyncLoanRepaymentsScreenRoute> {
        SyncLoanRepaymentTransactionScreenRoute {
            onBackPressed()
        }
    }
}

@Serializable
data object SyncCenterPayloadsScreenRoute

fun NavController.navigateToSyncCenterPayloadsScreen() {
    navigate(SyncCenterPayloadsScreenRoute)
}

@Serializable
data object SyncGroupPayloadsScreenRoute

fun NavController.navigateToSyncGroupPayloadsScreen() {
    navigate(SyncGroupPayloadsScreenRoute)
}

@Serializable
data object SyncClientPayloadsScreenRoute

fun NavController.navigateToSyncClientPayloadsScreen() {
    navigate(SyncClientPayloadsScreenRoute)
}

@Serializable
data object SyncSavingsAccountTransactionsScreenRoute

fun NavController.navigateToSyncSavingsAccountTransactionsScreen() {
    navigate(SyncSavingsAccountTransactionsScreenRoute)
}

@Serializable
data object SyncLoanRepaymentsScreenRoute

fun NavController.navigateToSyncLoanRepaymentScreen() {
    navigate(SyncLoanRepaymentsScreenRoute)
}

fun NavController.navigateToOfflineDashBoardScreen() {
    navigate(OfflineDashboardScreenRoute)
}
