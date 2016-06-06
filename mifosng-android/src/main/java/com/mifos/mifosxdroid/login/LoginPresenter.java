package com.mifos.mifosxdroid.login;

import com.mifos.App;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.User;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 4/6/16.
 */
public class LoginPresenter implements Presenter<LoginMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;
    private LoginMvpView mLoginMvpView;

    @Inject
    public LoginPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoginMvpView mvpView) {
        mLoginMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mLoginMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void login(String instanceURL, String username, String password) {
        App.apiManager.setupEndpoint(instanceURL);
        mLoginMvpView.showProgressbar(true);
        mSubscription = mDataManager.login(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mLoginMvpView.showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginMvpView.showProgressbar(false);
                        mLoginMvpView.onLoginError(e);
                    }

                    @Override
                    public void onNext(User user) {
                        mLoginMvpView.showProgressbar(false);
                        mLoginMvpView.onLoginSuccessful(user);
                    }
                });
    }
}
