/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.amountTransfer

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class AmountTransferScreenRoute(
    val fromClientId: Int,
    val fromAccountId: Int,
    // for loan its 1
    val fromAccountType: Int = 1,
    val fromAccountNumber: String? = null,
    val fromOfficeId: Int? = null,
    val currency: String? = null,
)

fun NavController.navigateToTransferScreen(
    fromAccountId: Int,
    fromAccountNumber: String,
    fromClientId: Int,
    currency: String,
    fromOfficeId: Int,
) {
    navigate(
        AmountTransferScreenRoute(
            fromClientId = fromClientId,
            fromAccountId = fromAccountId,
            fromAccountNumber = fromAccountNumber,
            fromOfficeId = fromOfficeId,
            currency = currency,
        ),
    )
}

fun NavGraphBuilder.amountTransferScreen(
    navController: NavController,
    onBackPressed: () -> Unit,
) {
    composable<AmountTransferScreenRoute> {
        AmountTransferScreenRoute(
            navController = navController,
            navigateBack = onBackPressed,
        )
    }
}
