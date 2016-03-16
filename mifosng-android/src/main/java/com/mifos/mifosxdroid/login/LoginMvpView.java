/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.login;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.User;

/**
 * Created by Rajan Maurya on 16/3/16.
 */
public interface LoginMvpView extends MvpView {

    void onLoginSuccessful(User user);

    void onLoginError(Throwable throwable);

    void showProgress(boolean show);
}
