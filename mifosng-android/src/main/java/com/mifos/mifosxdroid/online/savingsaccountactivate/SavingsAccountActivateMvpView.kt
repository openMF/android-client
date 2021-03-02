package com.mifos.mifosxdroid.online.savingsaccountactivate

import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by Tarun on 01/06/17.
 */
interface SavingsAccountActivateMvpView : MvpView {
    fun showUserInterface()
    fun showSavingAccountActivatedSuccessfully(genericResponse: GenericResponse?)
    fun showError(s: String?)
}