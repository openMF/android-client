package com.mifos.mifosxdroid.twofactor;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.twofactor.AccessToken;
import com.mifos.objects.twofactor.DeliveryMethod;

import java.util.List;


public interface TwoFactorMvpView extends MvpView {

    void showLoadingProgressbar();

    void showDeliveryFetchErrorToast();
    void showOTPRequestErrorToast();
    void showWrongTokenToast();

    void onDeliveryMethodsFetch(List<DeliveryMethod> deliveryMethods);

    void onOTPTokenValidated(AccessToken accessToken);

    void onOTPRequested();
}
