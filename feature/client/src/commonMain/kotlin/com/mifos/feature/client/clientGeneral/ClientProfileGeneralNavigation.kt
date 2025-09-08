/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientGeneral

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientProfileGeneralRoute(
    val id: Int = -1,
)

fun NavGraphBuilder.clientProfileGeneralDestination(
    navController: NavController,
    onNavigateBack: () -> Unit,
    upcomingCharges: (Int) -> Unit,
    loanAccounts: (Int) -> Unit,
    savingAccounts: (Int) -> Unit,
    fixedDepositAccounts: (Int) -> Unit,
    recurringDepositAccounts: (Int) -> Unit,
    sharesAccounts: (Int) -> Unit,
    collateralData: (Int) -> Unit,
) {
    composable<ClientProfileGeneralRoute> {
        ClientProfileGeneralScreen(
            onNavigateBack,
            navController,
            upcomingCharges,
            loanAccounts,
            savingAccounts,
            fixedDepositAccounts,
            recurringDepositAccounts,
            sharesAccounts,
            collateralData,

        )
    }
}

fun NavController.navigateToClientProfileGeneralRoute(id: Int) {
    this.navigate(
        ClientProfileGeneralRoute(id = id),
    )
}
