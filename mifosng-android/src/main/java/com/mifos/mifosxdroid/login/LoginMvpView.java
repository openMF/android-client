package com.mifos.mifosxdroid.login;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.user.User;

/**
 * Created by Rajan Maurya on 4/6/16.
 */
public interface LoginMvpView extends MvpView {

    void showToastMessage(String message);

    void onLoginSuccessful(User user);

    void onLoginError(String error);

}
