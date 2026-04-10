/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.savingsAccounts

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import kotlinx.serialization.Serializable

@Serializable
data class SavingsAccountsRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.savingsAccountsDestination(
    navigateBack: () -> Unit,
    navController: NavController,
    navigateToViewAccount: (Int, SavingAccountDepositTypeEntity) -> Unit,
    navigateToApproveAccount: (Int) -> Unit,
    createAccount: (Int) -> Unit,
) {
    composable<SavingsAccountsRoute> {
        SavingsAccountsScreen(
            navigateBack = navigateBack,
            navigateToViewAccount = navigateToViewAccount,
            navigateToApproveAccount = navigateToApproveAccount,
            createAccount = { clientId -> createAccount(clientId) },
            navController = navController,
        )
    }
}

fun NavController.navigateToClientSavingsAccountsRoute(
    clientId: Int,
) {
    this.navigate(SavingsAccountsRoute(clientId = clientId))
}
