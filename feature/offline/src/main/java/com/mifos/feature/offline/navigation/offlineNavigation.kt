package com.mifos.feature.offline.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.offline.dashboard.OfflineDashboardRoute

/**
 * Created by Pronay Sarker on 31/08/2024 (3:57 PM)
 */

fun NavGraphBuilder.offlineNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = OfflineScreens.OfflineDashboardScreens.route,
        route = "offline_graph"
    ) {
        offlineDashboardScreen(
            onBackPressed = navController::popBackStack,
            syncLoanRepayment = { },
            syncSavingsAccountTransactions = { },
            syncGroupPayload = { },
            syncClientPayload = { },
            syncCenterPayload = { }
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
        route = OfflineScreens.OfflineDashboardScreens.route
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

fun NavController.navigateToOfflineDashboardScreen() {
    navigate(OfflineScreens.OfflineDashboardScreens.route)
}