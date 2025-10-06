/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.fixedDepositAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class FixedDepositAccountRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.clientFixedDepositAccountDestination(
    navController: NavController,
    navigateBack: () -> Unit,
    onApproveAccount: (String) -> Unit,
    onViewAccount: (String) -> Unit,
) {
    composable<FixedDepositAccountRoute> {
        FixedDepositAccountScreen(
            navController,
            navigateBack = navigateBack,
            onApproveAccount = onApproveAccount,
            onViewAccount = onViewAccount,
            onCreateNew = { clientId ->
                // Navigate to the multi-step flow
                navController.navigateToFixedDepositFlow(clientId)
        )
    }
}
// Add the multi-step flow route
    composable<FixedDepositAccountFlowRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<FixedDepositAccountFlowRoute>()
        FixedDepositAccountFlowScreen(
            clientId = route.clientId,
            navigateBack = { navController.popBackStack() },
            onAccountCreated = { accountId ->
                // Navigate back to listing or details
                navController.popBackStack()
            }
        )
    }
}
@Serializable
data class FixedDepositAccountFlowRoute(
    val clientId: Int
)

fun NavController.navigateToFixedDepositFlow(clientId: Int) {
    this.navigate(FixedDepositAccountFlowRoute(clientId = clientId))
}

fun NavController.navigateToFixedDepositAccountRoute(
    clientId: Int,
) {
    this.navigate(FixedDepositAccountRoute(clientId = clientId))
}
