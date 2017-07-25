package com.mifos.mifosxdroid.twofactor;

import com.mifos.api.datamanager.DataManagerTwoFactor;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.twofactor.AccessToken;
import com.mifos.objects.twofactor.DeliveryMethod;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TwoFactorPresenter extends BasePresenter<TwoFactorMvpView> {

    private final DataManagerTwoFactor dataManagerTwoFactor;

    private ArrayList<DeliveryMethod> otpDeliveryMethods;

    private Subscription subscription;

    @Inject
    public TwoFactorPresenter(DataManagerTwoFactor dataManagerTwoFactor) {
        this.dataManagerTwoFactor = dataManagerTwoFactor;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }


    public void fetchDeliveryMethods() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        getMvpView().showLoadingProgressbar();
        subscription = dataManagerTwoFactor.getDeliveryMethods()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<DeliveryMethod>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDeliveryFetchErrorToast();
                    }

                    @Override
                    public void onNext(List<DeliveryMethod> deliveryMethods) {
                        getMvpView().showProgressbar(false);
                        otpDeliveryMethods = new ArrayList<DeliveryMethod>(deliveryMethods);
                        getMvpView().onDeliveryMethodsFetch(otpDeliveryMethods);
                    }
                });
    }

    public void requestOTP(int deliveryMethodId) {
        try {
            final DeliveryMethod deliveryMethod = otpDeliveryMethods.get(deliveryMethodId - 1);
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            getMvpView().showLoadingProgressbar();
            subscription = dataManagerTwoFactor.requestOTP(deliveryMethod.getName())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {
                            getMvpView().showProgressbar(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            getMvpView().showProgressbar(false);
                            getMvpView().showOTPRequestErrorToast();
                        }

                        @Override
                        public void onNext(String response) {
                            getMvpView().showProgressbar(false);
                            getMvpView().onOTPRequested();
                        }
                    });

        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    public void validateToken(String token) {
        getMvpView().showProgressbar(true);

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        getMvpView().showLoadingProgressbar();
        subscription = dataManagerTwoFactor.validateToken(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<AccessToken>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showWrongTokenToast();
                    }

                    @Override
                    public void onNext(AccessToken accessToken) {
                        getMvpView().showProgressbar(false);
                        getMvpView().onOTPTokenValidated(accessToken);
                    }
                });

    }
}
