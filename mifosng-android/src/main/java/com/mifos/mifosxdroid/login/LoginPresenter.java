/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.login;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;

import rx.Subscription;

/**
 * Created by Rajan Maurya on 16/3/16.
 */
public class LoginPresenter implements Presenter<LoginMvpView> {

    private final DataManager mDatamanager;
    private Subscription mSubscription;
    private LoginMvpView mLoginMvpView;

    public LoginPresenter(DataManager datamanager){
        mDatamanager = datamanager;
    }

    @Override
    public void attachView(LoginMvpView mvpView) {
        this.mLoginMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mLoginMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void login(String instanceURL, String username, String password){

    }
}
