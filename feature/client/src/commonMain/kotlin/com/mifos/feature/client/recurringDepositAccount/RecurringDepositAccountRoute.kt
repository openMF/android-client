/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.recurringDepositAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.navigateToRecurringAccountRoute
import kotlinx.serialization.Serializable

@Serializable
data class RecurringDepositAccountRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.clientRecurringDepositAccountDestination(
    navController: NavController,
    navigateBack: () -> Unit,
    onApproveAccount: (String) -> Unit,
    onViewAccount: (String) -> Unit,
) {
    composable<RecurringDepositAccountRoute> {
        RecurringDepositAccountScreen(
            navController = navController,
            navigateBack = navigateBack,
            onApproveAccount = {
                onApproveAccount(it)
            },
            onViewAccount = {
                onViewAccount(it)
            },
            createAccount = { clientId ->
                navController.navigateToRecurringAccountRoute(clientId)
            },
        )
    }
}

fun NavController.navigateToRecurringDepositAccountRoute(
    clientId: Int,
) {
    this.navigate(RecurringDepositAccountRoute(clientId = clientId))
}
