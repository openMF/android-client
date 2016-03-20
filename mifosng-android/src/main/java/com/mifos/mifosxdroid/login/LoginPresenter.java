/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.login;

import com.mifos.App;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.User;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        App.apiManager.setupEndpoint(instanceURL);
        mLoginMvpView.showProgress(true);
        mSubscription = mDatamanager.login(username,password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mLoginMvpView.showProgress(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginMvpView.showProgress(false);
                        mLoginMvpView.onLoginError(e);
                    }

                        @Override
                    public void onNext(User user) {
                        mLoginMvpView.showProgress(false);
                        mLoginMvpView.onLoginSuccessful(user);
                    }
                });
    }
}
