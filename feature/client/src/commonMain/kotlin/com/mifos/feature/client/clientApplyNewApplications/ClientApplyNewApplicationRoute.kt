/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientApplyNewApplications

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientApplyNewApplicationRoute(
    val clientId: Int,
    val status: String,
    val accountNo: String,
)

fun NavGraphBuilder.clientApplyNewApplicationRoute(
    onNavigateBack: () -> Unit,
    onNavigateApplyLoanAccount: (Int, String) -> Unit,
    onNavigateApplySavingsAccount: (Int) -> Unit,
    onNavigateApplyShareAccount: (Int) -> Unit,
    onNavigateApplyRecurringAccount: (Int) -> Unit,
    onNavigateApplyFixedAccount: (Int) -> Unit,
    navController: NavController,
) {
    composable<ClientApplyNewApplicationRoute> {
        ClientApplyNewApplicationsScreen(
            onNavigateBack = onNavigateBack,
            onNavigateApplyLoanAccount = onNavigateApplyLoanAccount,
            onNavigateApplySavingsAccount = onNavigateApplySavingsAccount,
            onNavigateApplyShareAccount = onNavigateApplyShareAccount,
            onNavigateApplyRecurringAccount = onNavigateApplyRecurringAccount,
            onNavigateApplyFixedAccount = onNavigateApplyFixedAccount,
            navController = navController,
        )
    }
}

fun NavController.navigateToClientApplyNewApplicationScreen(clientId: Int, status: String, accountNo: String) {
    this.navigate(ClientApplyNewApplicationRoute(clientId, status, accountNo))
}
