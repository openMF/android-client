/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountSummary

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import kotlinx.serialization.Serializable

@Serializable
data class LoanSummaryScreenRoute(
    val loanId: Int,
)

fun NavController.navigateToLoanSummaryScreen(loanId: Int) {
    navigate(LoanSummaryScreenRoute(loanId))
}

fun NavGraphBuilder.loanAccountSummary(
    onBackPressed: () -> Unit,
    onMoreInfoClicked: (String, Int) -> Unit,
    onTransactionsClicked: (loadId: Int) -> Unit,
    onRepaymentScheduleClicked: (loanId: Int) -> Unit,
    onDocumentsClicked: (Int) -> Unit,
    onChargesClicked: (Int) -> Unit,
    approveLoan: (loanId: Int) -> Unit,
    disburseLoan: (Int) -> Unit,
    onRepaymentClick: (LoanWithAssociations) -> Unit,
    navController: NavController,
) {
    composable<LoanSummaryScreenRoute> {
        LoanSummaryScreenRoute(
            onMoreInfoClicked = onMoreInfoClicked,
            onTransactionsClicked = onTransactionsClicked,
            onRepaymentScheduleClicked = onRepaymentScheduleClicked,
            onDocumentsClicked = onDocumentsClicked,
            onChargesClicked = onChargesClicked,
            approveLoan = approveLoan,
            disburseLoan = disburseLoan,
            onRepaymentClick = onRepaymentClick,
            onNavigateBack = onBackPressed,
            navController = navController,
        )
    }
}
