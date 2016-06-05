package com.mifos.mifosxdroid.login;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.User;

/**
 * Created by Rajan Maurya on 4/6/16.
 */
public interface LoginMvpView extends MvpView{

    void onLoginSuccessful(User user);

    void onLoginError(Throwable throwable);

}
