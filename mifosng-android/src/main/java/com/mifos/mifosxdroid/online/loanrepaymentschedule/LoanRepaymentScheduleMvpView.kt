package com.mifos.mifosxdroid.online.loanrepaymentschedule

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.accounts.loan.LoanWithAssociations

/**
 * Created by Rajan Maurya on 08/06/16.
 */
interface LoanRepaymentScheduleMvpView : MvpView {
    fun showLoanRepaySchedule(loanWithAssociations: LoanWithAssociations)
    fun showFetchingError(s: String?)
}