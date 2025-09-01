/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepayment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.serialization.Serializable

@Serializable
data class LoanRepaymentScreenRoute(
    var clientName: String = "",
    var loanId: Int = 0,
    var loanAccountNumber: String,
    var loanProductName: String,
    var amountInArrears: Double? = 0.0,
)

fun NavGraphBuilder.loanRepaymentScreen(
    onBackPressed: () -> Unit,
) {
    composable<LoanRepaymentScreenRoute> {
        LoanRepaymentScreen(
            navigateBack = onBackPressed,
        )
    }
}

fun NavController.navigateToLoanRepaymentScreen(loanWithAssociations: LoanWithAssociationsEntity) {
    navigate(
        LoanRepaymentScreenRoute(
            clientName = loanWithAssociations.clientName,
            loanId = loanWithAssociations.id,
            loanAccountNumber = loanWithAssociations.accountNo,
            loanProductName = loanWithAssociations.loanProductName,
            amountInArrears = loanWithAssociations.summary.totalOverdue,
        ),
    )
}
