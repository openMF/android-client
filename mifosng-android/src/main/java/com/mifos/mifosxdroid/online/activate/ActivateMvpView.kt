package com.mifos.mifosxdroid.online.activate

import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by Rajan Maurya on 09/02/17.
 */
interface ActivateMvpView : MvpView {
    fun showUserInterface()
    fun showActivatedSuccessfully(message: Int)
    fun showError(errorMessage: String)
}