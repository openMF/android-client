/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.feature.loan.loanAccountSummary.LoanAccountSummaryScreenRoute
import com.mifos.feature.loan.loanAccountSummary.loanAccountSummary
import com.mifos.feature.loan.loanApproval.LoanAccountApprovalScreen
import com.mifos.feature.loan.loanCharge.loanChargeScreen
import com.mifos.feature.loan.loanCharge.navigateToLoanChargesScreen
import com.mifos.feature.loan.loanDisbursement.loanDisbursementScreen
import com.mifos.feature.loan.loanDisbursement.navigateToLoanDisbursementScreen
import com.mifos.feature.loan.loanRepayment.loanRepaymentScreen
import com.mifos.feature.loan.loanRepayment.navigateToLoanRepaymentScreen
import com.mifos.feature.loan.loanRepaymentSchedule.loanRepaymentSchedule
import com.mifos.feature.loan.loanRepaymentSchedule.navigateToLoanRepaymentScheduleScreen
import com.mifos.feature.loan.loanTransaction.loanTransactionScreen
import com.mifos.feature.loan.loanTransaction.navigateToLoanTransactionScreen
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.serialization.Serializable

@Serializable
data object LoanAccountNavGraph

fun NavGraphBuilder.loanNavGraph(
    navController: NavController,
    onDocumentsClicked: (Int, String) -> Unit,
    onMoreInfoClicked: (String, Int) -> Unit,
) {
    navigation<LoanAccountNavGraph>(
        startDestination = LoanAccountSummaryScreenRoute(-1),
    ) {
        loanAccountSummary(
            onBackPressed = navController::popBackStack,
            onMoreInfoClicked = onMoreInfoClicked,
            onTransactionsClicked = navController::navigateToLoanTransactionScreen,
            onRepaymentScheduleClicked = navController::navigateToLoanRepaymentScheduleScreen,
            onDocumentsClicked = { onDocumentsClicked(it, Constants.ENTITY_TYPE_LOANS) },
            onChargesClicked = navController::navigateToLoanChargesScreen,
            approveLoan = navController::navigateToLoanApprovalScreen,
            disburseLoan = navController::navigateToLoanDisbursementScreen,
            onRepaymentClick = navController::navigateToLoanRepaymentScreen,
        )

        loanDisbursementScreen {
            navController.popBackStack()
        }
        loanApprovalScreen {
            navController.popBackStack()
        }
        loanRepaymentSchedule {
            navController.popBackStack()
        }
        loanTransactionScreen {
            navController.popBackStack()
        }
        loanChargeScreen {
            navController.popBackStack()
        }
        loanRepaymentScreen {
            navController.popBackStack()
        }
    }
}

fun NavGraphBuilder.loanApprovalScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = LoanScreens.LoanApprovalScreen.route,
        arguments = listOf(
            navArgument(name = "arg", builder = { type = NavType.StringType }),
        ),
    ) {
        LoanAccountApprovalScreen(
            navigateBack = onBackPressed,
        )
    }
}

fun NavController.navigateToLoanApprovalScreen(
    loanId: Int,
    loanWithAssociations: LoanWithAssociationsEntity,
) {
    navigate(LoanScreens.LoanApprovalScreen.argument(loanId, loanWithAssociations))
}
