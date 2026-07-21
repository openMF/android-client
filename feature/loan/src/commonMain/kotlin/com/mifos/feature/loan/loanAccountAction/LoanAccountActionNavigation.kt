/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountAction

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.loan.assignLoanOfficer.navigateToAssignLoanOfficerScreen
import kotlinx.serialization.Serializable

@Serializable
data class LoanAccountActionRoute(
    val loanId: Int,
)

fun NavGraphBuilder.loanAccountActionDestination(
    navController: NavController,
    onNavigateBack: () -> Unit,
    navigateToPaymentsActionScreen: () -> Unit,
    navigateToChargeOff: (loanId: Int) -> Unit,
    navigateToCreateGuarantor: (loanId: Int) -> Unit,
    navigateToAssignLoanOfficerScreen: (loanId: Int) -> Unit,
    navigateToDisburse: (loanId: Int) -> Unit,
) {
    composable<LoanAccountActionRoute> {
        LoanAccountActionScreen(
            navController = navController,
            onNavigateBack = onNavigateBack,
            onActionSelected = { loanAccountActionItem, loanId ->
                when (loanAccountActionItem) {
                    LoanAccountActionItem.AddCollateral -> {}
                    LoanAccountActionItem.AddInterestPause -> {}
                    LoanAccountActionItem.AddLoanCharge -> {}
                    LoanAccountActionItem.Approve -> {}
                    LoanAccountActionItem.AssignLoanOfficer -> navigateToAssignLoanOfficerScreen(loanId)
                    LoanAccountActionItem.BuyDownFee -> {}
                    LoanAccountActionItem.CapitalizedIncome -> {}
                    LoanAccountActionItem.ChangeLoanOfficer -> {}
                    LoanAccountActionItem.ChargeOff -> navigateToChargeOff(loanId)
                    LoanAccountActionItem.Close -> {}
                    LoanAccountActionItem.CloseAsRescheduled -> {}
                    LoanAccountActionItem.ContractTermination -> {}
                    LoanAccountActionItem.CreateGuarantors -> navigateToCreateGuarantor(loanId)
                    LoanAccountActionItem.CreditBalanceRefund -> {}
                    LoanAccountActionItem.Delete -> {}
                    LoanAccountActionItem.Disburse -> navigateToDisburse(loanId)
                    LoanAccountActionItem.DisburseToSavings -> {}
                    LoanAccountActionItem.EditRepaymentSchedule -> {}
                    LoanAccountActionItem.Foreclosure -> {}
                    LoanAccountActionItem.GoodwillCredit -> {}
                    LoanAccountActionItem.InterestPaymentWaiver -> {}
                    LoanAccountActionItem.LoanScreenReport -> {}
                    LoanAccountActionItem.MakeRepayment -> {}
                    LoanAccountActionItem.MerchantIssuedRefund -> {}
                    LoanAccountActionItem.ModifyApplication -> {}
                    LoanAccountActionItem.PaymentRefund -> {}
                    LoanAccountActionItem.Payments -> navigateToPaymentsActionScreen()
                    LoanAccountActionItem.PrepayLoan -> {}
                    LoanAccountActionItem.ReAge -> {}
                    LoanAccountActionItem.ReAmortize -> {}
                    LoanAccountActionItem.RecoverFromGuarantor -> {}
                    LoanAccountActionItem.RecoveryPayment -> {}
                    LoanAccountActionItem.Reject -> {}
                    LoanAccountActionItem.Reschedule -> {}
                    LoanAccountActionItem.SellLoan -> {}
                    LoanAccountActionItem.TransferFunds -> {}
                    LoanAccountActionItem.UndoApproval -> {}
                    LoanAccountActionItem.UndoChargeOff -> {}
                    LoanAccountActionItem.UndoDisbursal -> {}
                    LoanAccountActionItem.UndoLastDisbursal -> {}
                    LoanAccountActionItem.UndoReAge -> {}
                    LoanAccountActionItem.UndoReAmortize -> {}
                    LoanAccountActionItem.UndoWriteOff -> {}
                    LoanAccountActionItem.ViewGuarantors -> {}
                    LoanAccountActionItem.WithdrawnByClient -> {}
                    LoanAccountActionItem.WriteOff -> {}
                    LoanAccountActionItem.WaiveInterest -> {}
                }
            },
        )
    }
}

fun NavController.navigateToLoanAccountActionScreen(loanId: Int) {
    this.navigate(
        LoanAccountActionRoute(loanId = loanId),
    )
}

fun NavController.reloadLoanAccountActionScreen(loanId: Int) {
    navigate(LoanAccountActionRoute(loanId)) {
        popUpTo(LoanAccountActionRoute(loanId)) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
