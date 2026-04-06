/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDashboard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class LoanDashboardScreenRoute(
    val loanId: Int,
)

fun NavController.navigateToLoanDashboardScreen(loanId: Int) {
    navigate(LoanDashboardScreenRoute(loanId))
}

fun NavGraphBuilder.loanDashboardScreen(
    onNavigateBack: () -> Unit,
    navigateToTransactions: (Int) -> Unit,
    navigateToRejectLoan: (Int) -> Unit = {},
    navigateToApproveLoan: (Int) -> Unit = {},
    navigateToUndoApproval: (Int) -> Unit = {},
    navigateToDisburseLoan: (Int) -> Unit = {},
    navigateToMakeRepayment: (Int) -> Unit = {},
    navigateToCreditBalanceRefund: (Int) -> Unit = {},
) {
    composable<LoanDashboardScreenRoute> {
        LoanDashboardScreen(
            onNavigateBack = onNavigateBack,
            navigateToTransactions = navigateToTransactions,
            navigateToRejectLoan = navigateToRejectLoan,
            navigateToApproveLoan = navigateToApproveLoan,
            navigateToUndoApproval = navigateToUndoApproval,
            navigateToDisburseLoan = navigateToDisburseLoan,
            navigateToMakeRepayment = navigateToMakeRepayment,
            navigateToCreditBalanceRefund = navigateToCreditBalanceRefund,
        )
    }
}
