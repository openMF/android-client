package com.mifos.feature.offline.syncLoanRepaymentTransaction

import com.mifos.core.objects.PaymentTypeOption
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncLoanRepaymentTransactionUiState {

    data object ShowProgressbar : SyncLoanRepaymentTransactionUiState()

    data class ShowError(val message: Int) : SyncLoanRepaymentTransactionUiState()

    data class ShowLoanRepaymentTransactions(
        val loanRepaymentRequests: List<LoanRepaymentRequest>,
        val paymentTypeOptions: List<PaymentTypeOption>
    ) : SyncLoanRepaymentTransactionUiState()

    data class ShowEmptyLoanRepayments(val message: String) : SyncLoanRepaymentTransactionUiState()
}