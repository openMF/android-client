package com.mifos.mifosxdroid.privacypolicy;

import com.mifos.mifosxdroid.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by Tarun on 23-02-19.
 */

public class PrivacyPolicyPresenter extends BasePresenter<PrivacyPolicyMvpView> {
    @Inject
    public PrivacyPolicyPresenter() {}

    @Override
    public void attachView(PrivacyPolicyMvpView mvpView) {
        super.attachView(mvpView);
    }
}
