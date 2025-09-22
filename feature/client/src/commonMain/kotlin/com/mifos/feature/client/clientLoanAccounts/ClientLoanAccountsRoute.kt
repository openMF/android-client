/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientLoanAccounts

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientLoanAccountsRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.clientLoanAccountsDestination(
    navigateBack: () -> Unit,
    navController: NavController,
    navigateToViewAccount: (Int) -> Unit,
    navigateToMakeRepayment: (Int) -> Unit,
) {
    composable<ClientLoanAccountsRoute> {
        ClientLoanAccountsScreenRoute(
            navigateBack = navigateBack,
            viewAccount = navigateToViewAccount,
            makeRepayment = navigateToMakeRepayment,
            navController = navController,
        )
    }
}

fun NavController.navigateToClientLoanAccountsRoute(
    clientId: Int,
) {
    this.navigate(ClientLoanAccountsRoute(clientId = clientId))
}
