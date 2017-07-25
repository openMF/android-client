package com.mifos.api.datamanager;


import com.mifos.api.BaseApiManager;
import com.mifos.objects.twofactor.AccessToken;
import com.mifos.objects.twofactor.DeliveryMethod;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class DataManagerTwoFactor {

    private final BaseApiManager baseApiManager;

    @Inject
    public DataManagerTwoFactor(BaseApiManager baseApiManager) {
        this.baseApiManager = baseApiManager;
    }

    public Observable<List<DeliveryMethod>> getDeliveryMethods() {
        return baseApiManager.getTwoFactorService().getDeliveryMethods();
    }

    public Observable<String> requestOTP(String deliveryMethod) {
        return baseApiManager.getTwoFactorService().requestOTP(deliveryMethod);
    }

    public Observable<AccessToken> validateToken(String token) {
        return baseApiManager.getTwoFactorService().validateToken(token);
    }
}
