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

@Serializable
data class LoanAccountRoute(
    val loanId: Int = -1,
)

fun NavGraphBuilder.loanProfileAccountDestination(
    onNavigateBack: () -> Unit,
    navController: NavController,
    navigateToRepaymentSchedule: (Int) -> Unit,
    navigateToTransactions: (Int) -> Unit,
    navigateToCharges: (Int) -> Unit,
    navigateToDocuments: (Int) -> Unit,
    navigateToReschedules: (Int) -> Unit,
    navigateToNotes: (Int) -> Unit,
    navigateToLoanAction: (Int) -> Unit,
    navigateToDashboard: (Int) -> Unit,
    approveLoan: (Int, LoanWithAssociationsEntity) -> Unit,
    onRepaymentClick: (LoanWithAssociationsEntity) -> Unit,
    navigateToTransferScreen: (loanId: Int) -> Unit,
) {
    composable<LoanAccountRoute> {
        LoanAccountProfileScreen(
            navController = navController,
            onNavigateBack = onNavigateBack,
            navigateToRepaymentSchedule = navigateToRepaymentSchedule,
            navigateToTransactions = navigateToTransactions,
            navigateToCharges = navigateToCharges,
            navigateToDocuments = navigateToDocuments,
            navigateToReschedules = navigateToReschedules,
            navigateToDashboard = navigateToDashboard,
            navigateToNotes = navigateToNotes,
            approveLoan = approveLoan,
            onRepaymentClick = onRepaymentClick,
            navigateToTransferScreen = navigateToTransferScreen,
            navigateToLoanAction = navigateToLoanAction,
        )
    }
}

fun NavController.navigateToLoanAccountProfileScreen(loanId: Int) {
    this.navigate(
        LoanAccountRoute(
            loanId = loanId,
        ),
    )
}

fun NavController.reloadLoanAccountProfileScreen(loanId: Int) {
    navigate(LoanAccountRoute(loanId)) {
        popUpTo(LoanAccountRoute(loanId)) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
