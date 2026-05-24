/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.mifos.core.common.utils.Constants
import com.mifos.feature.loan.amountTransfer.amountTransferScreen
import com.mifos.feature.loan.amountTransfer.navigateToTransferScreen
import com.mifos.feature.loan.assignLoanOfficer.assignLoanOfficerScreen
import com.mifos.feature.loan.assignLoanOfficer.navigateToAssignLoanOfficerScreen
import com.mifos.feature.loan.createGuarantor.createGuarantorScreen
import com.mifos.feature.loan.createGuarantor.navigateToCreateGuarantorScreen
import com.mifos.feature.loan.createLoanReschedules.loanRescheduleFormScreen
import com.mifos.feature.loan.loanAccountGeneral.loanAccountGeneralDestination
import com.mifos.feature.loan.loanAccountGeneral.navigateToLoanAccountGeneralScreen
import com.mifos.feature.loan.loanAccountAction.loanAccountActionDestination
import com.mifos.feature.loan.loanAccountAction.navigateToLoanAccountActionScreen
import com.mifos.feature.loan.loanAccountAction.payments.loanPaymentsActionDestination
import com.mifos.feature.loan.loanAccountAction.payments.navigateToLoanPaymentsAction
import com.mifos.feature.loan.loanAccountAction.reloadLoanAccountActionScreen
import com.mifos.feature.loan.loanAccountProfile.loanProfileAccountDestination
import com.mifos.feature.loan.loanAccountProfile.reloadLoanAccountProfileScreen
import com.mifos.feature.loan.loanAccountSummary.loanAccountSummary
import com.mifos.feature.loan.loanApproval.loanApprovalDestination
import com.mifos.feature.loan.loanApproval.navigateToLoanApprovalScreen
import com.mifos.feature.loan.loanCharge.loanChargeScreen
import com.mifos.feature.loan.loanCharge.navigateToLoanChargesScreen
import com.mifos.feature.loan.loanChargeOff.loanChargeOffScreen
import com.mifos.feature.loan.loanChargeOff.navigateToLoanChargeOffScreen
import com.mifos.feature.loan.loanDashboard.loanDashboardScreen
import com.mifos.feature.loan.loanDashboard.navigateToLoanDashboardScreen
import com.mifos.feature.loan.loanDisburse.loanDisburseScreen
import com.mifos.feature.loan.loanDisburse.navigateToLoanDisburseScreen
import com.mifos.feature.loan.loanReject.loanRejectScreen
import com.mifos.feature.loan.loanReject.navigateToLoanRejectScreen
import com.mifos.feature.loan.loanRepayment.loanRepaymentScreen
import com.mifos.feature.loan.loanRepayment.navigateToLoanRepaymentScreen
import com.mifos.feature.loan.loanRepaymentSchedule.loanRepaymentSchedule
import com.mifos.feature.loan.loanRepaymentSchedule.navigateToLoanRepaymentScheduleScreen
import com.mifos.feature.loan.loanReschedules.loanReschedulesScreen
import com.mifos.feature.loan.loanReschedules.navigateToLoanReschedulesScreen
import com.mifos.feature.loan.loanTransaction.loanTransactionScreen
import com.mifos.feature.loan.loanTransaction.navigateToLoanTransactionScreen
import com.mifos.feature.loan.newLoanAccount.newLoanAccountDestination
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity

fun NavGraphBuilder.loanDestination(
    navController: NavController,
    onDocumentsClicked: (Int, String) -> Unit,
    onNotesClicked: (Int, String?) -> Unit,
    onMoreInfoClicked: (String, Int) -> Unit,
    onLoanCreated: (clientId: Int) -> Unit,
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
        navController = navController,
    )

    createGuarantorScreen(
        navigateBack = navController::popBackStack,
    )

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
    loanDashboardScreen(
        onNavigateBack = navController::popBackStack,
        navigateToTransactions = navController::navigateToLoanTransactionScreen,
    )
    loanChargeOffScreen(
        onNavigateBack = navController::navigateUp,
        onChargeOffSuccess = navController::reloadLoanAccountActionScreen,
    )
    loanRejectScreen(
        onNavigateBack = navController::navigateUp,
        onRejectSuccess = navController::reloadLoanAccountProfileScreen,
    )
    assignLoanOfficerScreen(
        navigateBack = navController::navigateUp,
        onAssignLoanOfficerSuccess = navController::reloadLoanAccountActionScreen,
    )
    newLoanAccountDestination(
        onNavigateBack = navController::popBackStack,
        onFinish = navController::popBackStack,
        onLoanCreated = onLoanCreated,
        navController = navController,
    )
    loanAccountGeneralDestination(
        navController = navController,
    )

    loanProfileAccountDestination(
        onNavigateBack = navController::popBackStack,
        navController = navController,
        approveLoan = navController::navigateToLoanApprovalScreen,
        onRepaymentClick = navController::navigateToLoanRepaymentScreen,
        navigateToGeneral = navController::navigateToLoanAccountGeneralScreen,
        navigateToRepaymentSchedule = navController::navigateToLoanRepaymentScheduleScreen,
        navigateToTransactions = navController::navigateToLoanTransactionScreen,
        navigateToCharges = navController::navigateToLoanChargesScreen,
        navigateToNotes = { loanId ->
            onNotesClicked(loanId, Constants.ENTITY_TYPE_LOANS)
        },
        navigateToDocuments = { loanId ->
            onDocumentsClicked(loanId, Constants.ENTITY_TYPE_LOANS)
        },
        navigateToDashboard = navController::navigateToLoanDashboardScreen,
        navigateToTransferScreen = navController::navigateToTransferScreen,
        navigateToLoanAction = navController::navigateToLoanAccountActionScreen,
        navigateToReschedules = navController::navigateToLoanReschedulesScreen,
    )

    loanAccountActionDestination(
        navController = navController,
        onNavigateBack = navController::popBackStack,
        navigateToPaymentsActionScreen = navController::navigateToLoanPaymentsAction,
        navigateToChargeOff = navController::navigateToLoanChargeOffScreen,
        navigateToReject = navController::navigateToLoanRejectScreen,
        navigateToCreateGuarantor = navController::navigateToCreateGuarantorScreen,
        navigateToAssignLoanOfficerScreen = navController::navigateToAssignLoanOfficerScreen,
        navigateToDisburse = navController::navigateToLoanDisburseScreen,
    )

    loanPaymentsActionDestination(
        navController = navController,
        onNavigateBack = navController::popBackStack,
        onGoodwillCreditClick = {},
        onInterestPaymentWaiverClick = {},
        onPaymentRefundClick = {},
        onMerchantIssuedRefundClick = {},
    )

    amountTransferScreen(
        navController = navController,
        onBackPressed = navController::popBackStack,
    )

    loanReschedulesScreen(
        navController = navController,
        onBackPressed = navController::popBackStack,
    )

    loanRescheduleFormScreen(
        navController = navController,
        onBackPressed = navController::popBackStack,
        onDetailItemClick = { item, loanId ->
        onDetailItemClick = { loanId, item ->
            when (item) {
                LoanAccountProfileActionItem.General ->
                    navController.navigateToLoanAccountGeneralScreen(loanId = loanId)
                else -> Unit
            }
        },
    )

    loanAccountGeneralDestination(
        navController = navController,
    )

    loanDisburseScreen(
        onNavigateBack = navController::navigateUp,
        onDisburseSuccess = navController::reloadLoanAccountActionScreen,
    )
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
