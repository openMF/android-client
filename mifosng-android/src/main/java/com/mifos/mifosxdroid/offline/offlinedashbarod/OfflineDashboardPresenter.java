package com.mifos.mifosxdroid.offline.offlinedashbarod;

import com.mifos.mifosxdroid.base.BasePresenter;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 20/07/16.
 */
public class OfflineDashboardPresenter extends BasePresenter<OfflineDashboardMvpView> {


    private final CompositeSubscription mSubscriptions;

    @Inject
    public OfflineDashboardPresenter() {
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(OfflineDashboardMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }


}
