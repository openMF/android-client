/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.creditBalanceRefund

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.serialization.Serializable

@Serializable
data class CreditBalanceRefundScreenRoute(
    val loanId: Int,
    val clientName: String,
    val loanAccountNumber: String,
    val overpaidAmount: Double,
    val currencyCode: String? = null,
    val decimalPlaces: Int? = null,
)

fun NavGraphBuilder.creditBalanceRefundScreen(
    navController: NavController,
    onBackPressed: () -> Unit,
) {
    composable<CreditBalanceRefundScreenRoute> {
        CreditBalanceRefundScreen(
            navigateBack = onBackPressed,
            navController = navController,
        )
    }
}

fun NavController.navigateToCreditBalanceRefundScreen(loanWithAssociations: LoanWithAssociationsEntity) {
    navigate(
        CreditBalanceRefundScreenRoute(
            loanId = loanWithAssociations.id,
            clientName = loanWithAssociations.clientName,
            loanAccountNumber = loanWithAssociations.accountNo,
            overpaidAmount = loanWithAssociations.totalOverpaid,
            currencyCode = loanWithAssociations.currency.code,
            decimalPlaces = loanWithAssociations.currency.decimalPlaces,
        ),
    )
}
