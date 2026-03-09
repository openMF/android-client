/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.recurringDeposit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountScreen
import com.mifos.feature.recurringDeposit.recurringAccountApproval.RecurringDepositAccountApprovalScreen
import kotlinx.serialization.Serializable

@Serializable
data class RecurringAccountRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.recurringAccountDestination(
    navController: NavController,
) {
    composable<RecurringAccountRoute> {
        RecurringAccountScreen(
            navController = navController,
            onNavigateBack = navController::popBackStack,
            onFinish = {},
        )
    }
}

fun NavController.navigateToRecurringAccountRoute(clientId: Int) {
    this.navigate(
        RecurringAccountRoute(clientId = clientId),
    )
}



@Serializable
data class RecurringDepositAccountApprovalRoute(
    val accountId: String,
)

fun NavGraphBuilder.recurringDepositAccountApprovalDestination(
    navController: NavController,
    navigateBack: () -> Unit,
) {
    composable<RecurringDepositAccountApprovalRoute> {
        RecurringDepositAccountApprovalScreen(
            navController = navController,
            navigateBack = navigateBack,
        )
    }
}

fun NavController.navigateToRecurringDepositAccountApproval(
    accountId: String,
) {
    this.navigate(RecurringDepositAccountApprovalRoute(accountId = accountId))
}
