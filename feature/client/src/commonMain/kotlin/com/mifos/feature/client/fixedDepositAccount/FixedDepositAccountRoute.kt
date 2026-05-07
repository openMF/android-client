/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.fixedDepositAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.client.newFixedDepositAccount.navigateToCreateFixedDepositRoute
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
            createAccount = { clientId -> navController.navigateToCreateFixedDepositRoute(clientId) },
        )
    }
}

fun NavController.navigateToFixedDepositAccountRoute(
    clientId: Int,
) {
    this.navigate(FixedDepositAccountRoute(clientId = clientId))
}
