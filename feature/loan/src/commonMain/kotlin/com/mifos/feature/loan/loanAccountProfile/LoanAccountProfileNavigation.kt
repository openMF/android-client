/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountProfile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.serialization.Serializable

/**
 * Navigation route for the Loan Account Profile screen.
 *
 * @property loanId The unique identifier of the loan account to display.
 */
@Serializable
data class LoanAccountRoute(
    val loanId: Int = -1,
)

/**
 * Registers the Loan Account Profile destination in the navigation graph.
 *
 * @param onNavigateBack Callback for the back button.
 * @param navController The [NavController] to manage navigation.
 * @param navigateToRepaymentSchedule Destination for repayment schedule.
 * @param navigateToTransactions Destination for transactions.
 * @param navigateToCharges Destination for charges.
 * @param navigateToDocuments Destination for documents.
 * @param navigateToReschedules Destination for reschedules.
 * @param navigateToNotes Destination for notes.
 * @param approveLoan Action for loan approval.
 * @param onRepaymentClick Action for repayment.
 * @param navigateToTransferScreen Destination for account transfer.
 * @param navigateToCloseLoan Destination for closing the loan.
 */
fun NavGraphBuilder.loanProfileAccountDestination(
    onNavigateBack: () -> Unit,
    navController: NavController,
    navigateToRepaymentSchedule: (Int) -> Unit,
    navigateToTransactions: (Int) -> Unit,
    navigateToCharges: (Int) -> Unit,
    navigateToDocuments: (Int) -> Unit,
    navigateToReschedules: (Int) -> Unit,
    navigateToNotes: (Int) -> Unit,
    approveLoan: (Int, LoanWithAssociationsEntity) -> Unit,
    onRepaymentClick: (LoanWithAssociationsEntity) -> Unit,
    navigateToTransferScreen: (loanId: Int, accountNumber: String, clientId: Int, currencyCode: String, officeId: Int) -> Unit,
    navigateToCloseLoan: (loanId: Int) -> Unit,
) {
    composable<LoanAccountRoute> {
        LoanAccountProfileScreen(
            onNavigateBack = onNavigateBack,
            navController = navController,
            navigateToRepaymentSchedule = navigateToRepaymentSchedule,
            navigateToTransactions = navigateToTransactions,
            navigateToCharges = navigateToCharges,
            navigateToDocuments = navigateToDocuments,
            navigateToReschedules = navigateToReschedules,
            navigateToNotes = navigateToNotes,
            approveLoan = approveLoan,
            onRepaymentClick = onRepaymentClick,
            navigateToTransferScreen = navigateToTransferScreen,
            navigateToCloseLoan = navigateToCloseLoan,
        )
    }
}

/**
 * Navigates to the Loan Account Profile screen.
 *
 * @param loanId The unique identifier of the loan.
 */
fun NavController.navigateToLoanAccountProfileScreen(loanId: Int) {
    this.navigate(
        LoanAccountRoute(
            loanId = loanId,
        ),
    )
}
