package com.mifos.mifosxdroid.online.loanaccountdisbursement

import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.templates.loans.LoanTransactionTemplate

/**
 * Created by Rajan Maurya on 8/6/16.
 */
interface LoanAccountDisbursementMvpView : MvpView {
    fun showUserInterface()
    fun showDisbursementDate(date: String?)
    fun showLoanTransactionTemplate(loanTransactionTemplate: LoanTransactionTemplate)
    fun showDisburseLoanSuccessfully(genericResponse: GenericResponse?)
    fun showError(s: String?)
    fun showError(errorMessage: Int)
}