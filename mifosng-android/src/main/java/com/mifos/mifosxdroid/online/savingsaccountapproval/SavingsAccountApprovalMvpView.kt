package com.mifos.mifosxdroid.online.savingsaccountapproval

import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by Rajan Maurya on 09/06/16.
 */
interface SavingsAccountApprovalMvpView : MvpView {
    fun showUserInterface()
    fun showSavingAccountApprovedSuccessfully(genericResponse: GenericResponse?)
    fun showError(s: String?)
}