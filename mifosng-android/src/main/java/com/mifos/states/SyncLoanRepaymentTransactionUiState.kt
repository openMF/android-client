package com.mifos.states

import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.loan.LoanRepaymentRequest

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncLoanRepaymentTransactionUiState {

    object ShowProgressbar : SyncLoanRepaymentTransactionUiState()

    data class ShowError(val message: Int) : SyncLoanRepaymentTransactionUiState()

    data class ShowLoanRepaymentTransactions(val loanRepaymentRequests: List<LoanRepaymentRequest>) :
        SyncLoanRepaymentTransactionUiState()

    data class ShowPaymentTypeOption(val paymentTypeOptions: List<PaymentTypeOption>) :
        SyncLoanRepaymentTransactionUiState()

    data class ShowPaymentFailed(val message: String) : SyncLoanRepaymentTransactionUiState()

    object ShowPaymentSubmittedSuccessfully : SyncLoanRepaymentTransactionUiState()

    data class ShowLoanRepaymentDeletedAndUpdateLoanRepayment(val loanRepaymentRequests: List<LoanRepaymentRequest>) :
        SyncLoanRepaymentTransactionUiState()

    data class ShowLoanRepaymentUpdated(val loanRepaymentRequest: LoanRepaymentRequest) :
        SyncLoanRepaymentTransactionUiState()
}