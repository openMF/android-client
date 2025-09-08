/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountSummary

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.serialization.Serializable

@Serializable
data class LoanAccountSummaryScreenRoute(
    val loanAccountNumber: Int,
)

fun NavController.navigateToLoanAccountSummaryScreen(loanAccountNumber: Int) {
    navigate(LoanAccountSummaryScreenRoute(loanAccountNumber))
}

fun NavGraphBuilder.loanAccountSummary(
    onBackPressed: () -> Unit,
    onMoreInfoClicked: (String, Int) -> Unit,
    onTransactionsClicked: (loadId: Int) -> Unit,
    onRepaymentScheduleClicked: (loanId: Int) -> Unit,
    onDocumentsClicked: (Int) -> Unit,
    onChargesClicked: (Int) -> Unit,
    approveLoan: (loadId: Int, loanWithAssociations: LoanWithAssociationsEntity) -> Unit,
    disburseLoan: (Int) -> Unit,
    onRepaymentClick: (LoanWithAssociationsEntity) -> Unit,
) {
    composable<LoanAccountSummaryScreenRoute> {
        LoanAccountSummaryScreen(
            navigateBack = onBackPressed,
            onMoreInfoClicked = onMoreInfoClicked,
            onTransactionsClicked = onTransactionsClicked,
            onRepaymentScheduleClicked = onRepaymentScheduleClicked,
            onDocumentsClicked = onDocumentsClicked,
            onChargesClicked = onChargesClicked,
            approveLoan = approveLoan,
            disburseLoan = disburseLoan,
            onRepaymentClick = onRepaymentClick,
        )
    }
}
