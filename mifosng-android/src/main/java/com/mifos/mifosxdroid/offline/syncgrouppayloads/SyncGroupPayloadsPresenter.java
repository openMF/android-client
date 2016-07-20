package com.mifos.mifosxdroid.offline.syncgrouppayloads;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.base.BasePresenter;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 19/07/16.
 */
public class SyncGroupPayloadsPresenter extends BasePresenter<SyncGroupPayloadsMvpView> {

    public final DataManagerGroups mDataManagerGroups;
    public CompositeSubscription mSubscriptions;

    @Inject
    public SyncGroupPayloadsPresenter(DataManagerGroups dataManagerGroups) {
        mDataManagerGroups = dataManagerGroups;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SyncGroupPayloadsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loanDatabaseGroupPayload() {

    }
}
