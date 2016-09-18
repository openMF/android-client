package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.base.BasePresenter;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 11/09/16.
 */
public class SyncGroupsDialogPresenter extends BasePresenter<SyncGroupsDialogMvpView> {

    private final DataManagerGroups mDataManagerGroups;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SyncGroupsDialogPresenter(DataManagerGroups dataManagerGroups) {
        mDataManagerGroups = dataManagerGroups;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SyncGroupsDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.clear();
    }


}
