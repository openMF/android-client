package com.mifos.mifosxdroid.online.loantransactions

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.accounts.loan.LoanWithAssociations

/**
 * Created by Rajan Maurya on 7/6/16.
 */
interface LoanTransactionsMvpView : MvpView {
    fun showLoanTransaction(loanWithAssociations: LoanWithAssociations)
    fun showFetchingError(s: String?)
}