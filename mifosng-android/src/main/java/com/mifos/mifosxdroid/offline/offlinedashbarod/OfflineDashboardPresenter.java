package com.mifos.mifosxdroid.offline.offlinedashbarod;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.group.GroupPayload;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 20/07/16.
 */
public class OfflineDashboardPresenter extends BasePresenter<OfflineDashboardMvpView> {


    private final CompositeSubscription mSubscriptions;
    private final DataManagerClient mDataManagerClient;
    private final DataManagerGroups mDataManagerGroups;

    @Inject
    public OfflineDashboardPresenter(DataManagerClient dataManagerClient,
                                     DataManagerGroups dataManagerGroups) {
        mSubscriptions = new CompositeSubscription();
        mDataManagerClient = dataManagerClient;
        mDataManagerGroups = dataManagerGroups;
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

    public void loadDatabaseClientPayload() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.getAllDatabaseClientPayload()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<ClientPayload>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError("Failed to load ClientPayload");
                    }

                    @Override
                    public void onNext(List<ClientPayload> clientPayloads) {
                        getMvpView().showClients(clientPayloads);
                        getMvpView().showProgressbar(false);
                    }
                }));
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
                        getMvpView().showError("Failed to load GroupPayload");
                    }

                    @Override
                    public void onNext(List<GroupPayload> groupPayloads) {
                        getMvpView().showGroups(groupPayloads);
                        getMvpView().showProgressbar(false);
                    }
                }));
    }

}
