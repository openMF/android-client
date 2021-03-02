package com.mifos.mifosxdroid.online.loanaccountsummary

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.accounts.loan.LoanWithAssociations

/**
 * Created by Rajan Maurya on 07/06/16.
 */
interface LoanAccountSummaryMvpView : MvpView {
    fun showLoanById(loanWithAssociations: LoanWithAssociations)
    fun showFetchingError(s: String?)
}