package com.mifos.mifosxdroid.online.loanaccountapproval

import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by Rajan Maurya on 8/6/16.
 */
interface LoanAccountApprovalMvpView : MvpView {
    fun showUserInterface()
    fun showLoanApproveSuccessfully(genericResponse: GenericResponse?)
    fun showLoanApproveFailed(s: String?)
}