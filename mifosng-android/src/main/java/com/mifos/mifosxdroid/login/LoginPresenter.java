package com.mifos.mifosxdroid.login;

import com.mifos.api.datamanager.DataManagerAuth;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.user.User;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 4/6/16.
 */
public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private final DataManagerAuth dataManagerAuth;
    private Subscription subscription;

    @Inject
    public LoginPresenter(DataManagerAuth dataManager) {
        dataManagerAuth = dataManager;
    }

    @Override
    public void attachView(LoginMvpView mvpView) {
        super.attachView(mvpView);
    }


    @Override
    public void detachView() {
        if (subscription != null) subscription.unsubscribe();
    }

    public void login(String username, String password) {
        getMvpView().showProgressbar(true);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = dataManagerAuth.login(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        String errorMessage;
                        try {
                            if (e instanceof HttpException) {
                                errorMessage = ((HttpException) e).response().errorBody().string();
                                getMvpView().onLoginError( MFErrorParser.parseError(errorMessage)
                                        .getDeveloperMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getInstance().getErrorHandler().handleError(throwable);
                        }
                    }

                    @Override
                    public void onNext(User user) {
                        getMvpView().showProgressbar(false);
                        getMvpView().onLoginSuccessful(user);
                    }
                });
    }
}
