/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.creditBalanceRefund

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class CreditBalanceRefundScreenRoute(
    val loanId: Int,
)

fun NavGraphBuilder.creditBalanceRefundScreen(
    navController: NavController,
    onBackPressed: () -> Unit,
    onRefreshParent: () -> Unit = {},
) {
    composable<CreditBalanceRefundScreenRoute> {
        CreditBalanceRefundScreen(
            navigateBack = onBackPressed,
            navController = navController,
            onRefreshParent = onRefreshParent,
        )
    }
}

fun NavController.navigateToCreditBalanceRefundScreen(loanId: Int) {
    navigate(CreditBalanceRefundScreenRoute(loanId = loanId))
}
