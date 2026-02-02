/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.recurringDeposit.recurringAccountApproval

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

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
