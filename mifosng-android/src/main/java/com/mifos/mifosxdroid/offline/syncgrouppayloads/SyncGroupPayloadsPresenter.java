package com.mifos.mifosxdroid.offline.syncgrouppayloads;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.group.GroupPayload;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.getAllDatabaseGroupPayload()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<GroupPayload>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupSyncFailed("Failed to load GroupPayload");
                    }

                    @Override
                    public void onNext(List<GroupPayload> groupPayloads) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupSyncResponse(groupPayloads);
                    }
                }));
    }
}
