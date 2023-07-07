package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.loan.LoanRepaymentRequest

/**
 * Created by Rajan Maurya on 28/07/16.
 */
interface SyncLoanRepaymentTransactionMvpView : MvpView {
    fun showLoanRepaymentTransactions(loanRepaymentRequests: List<LoanRepaymentRequest>)
    fun showPaymentTypeOption(paymentTypeOptions: List<PaymentTypeOption>)
    fun showOfflineModeDialog()
    fun showPaymentSubmittedSuccessfully()
    fun showPaymentFailed(errorMessage: String)
    fun showLoanRepaymentUpdated(loanRepaymentRequest: LoanRepaymentRequest)
    fun showLoanRepaymentDeletedAndUpdateLoanRepayment(
        loanRepaymentRequests: List<LoanRepaymentRequest>
    )

    fun showError(stringId: Int)
}