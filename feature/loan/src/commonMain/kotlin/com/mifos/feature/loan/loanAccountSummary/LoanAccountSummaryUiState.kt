/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountSummary

import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity

data class LoanAccountSummaryState(
    val loanWithAssociations: LoanWithAssociationsEntity? = null,
    val dialogState: DialogState? = null,
    val showLoanIdCopiedMessage: Boolean = false,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }
}

enum class LoanPrimaryAction {
    MAKE_REPAYMENT,
    APPROVE_LOAN,
    DISBURSE_LOAN,
    OVERPAID,
    CLOSED
}

sealed interface LoanAccountSummaryEvent {
    data object NavigateBack : LoanAccountSummaryEvent
    data class NavigateToMoreInfo(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToTransactions(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToRepaymentSchedule(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToDocuments(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToCharges(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToApproveLoan(
        val loanId: Int,
        val loanWithAssociations: LoanWithAssociationsEntity,
    ) : LoanAccountSummaryEvent
    data class NavigateToDisburseLoan(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToMakeRepayment(val loanWithAssociations: LoanWithAssociationsEntity) : LoanAccountSummaryEvent
}
sealed interface LoanAccountSummaryAction {
    data object OnRetry : LoanAccountSummaryAction
    data object NavigateBack : LoanAccountSummaryAction
    data object OnMoreInfoClick : LoanAccountSummaryAction
    data object OnTransactionsClick : LoanAccountSummaryAction
    data object OnRepaymentScheduleClick : LoanAccountSummaryAction
    data object OnDocumentsClick : LoanAccountSummaryAction
    data object OnChargesClick : LoanAccountSummaryAction
    data class OnApproveLoan(val loanWithAssociations: LoanWithAssociationsEntity) : LoanAccountSummaryAction
    data object OnDisburseLoan : LoanAccountSummaryAction
    data class OnMakeRepayment(val loanWithAssociations: LoanWithAssociationsEntity) : LoanAccountSummaryAction
    data object OnLoanIdCopied : LoanAccountSummaryAction
    data object OnMessageShown : LoanAccountSummaryAction
}
