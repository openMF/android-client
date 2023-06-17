package com.mifos.mifosxdroid.login

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.user.User

/**
 * Created by Rajan Maurya on 4/6/16.
 */
interface LoginMvpView : MvpView {
    fun showToastMessage(message: String)
    fun onLoginSuccessful(user: User)
    fun onLoginError(error: String)
}