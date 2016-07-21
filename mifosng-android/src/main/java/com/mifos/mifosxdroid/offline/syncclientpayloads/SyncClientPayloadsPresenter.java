package com.mifos.mifosxdroid.offline.syncclientpayloads;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.ClientPayload;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 08/07/16.
 */
public class SyncClientPayloadsPresenter extends BasePresenter<SyncClientPayloadsMvpView> {


    private final DataManagerClient mDataManagerClient;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SyncClientPayloadsPresenter(DataManagerClient dataManagerClient) {
        mDataManagerClient = dataManagerClient;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SyncClientPayloadsMvpView mvpView) {
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
                        getMvpView().showProgressbar(false);
                        getMvpView().showPayloads(clientPayloads);
                    }
                }));
    }

    public void syncClientPayload(ClientPayload clientPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.createClient(clientPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Client>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientSyncFailed();
                    }

                    @Override
                    public void onNext(Client client) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSyncResponse();
                    }
                }));
    }


    public void deleteAndUpdateClientPayload(int id) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.deleteAndUpdatePayloads(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<ClientPayload>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError("Failed Update List");
                    }

                    @Override
                    public void onNext(List<ClientPayload> clientPayloads) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showPayloadDeletedAndUpdatePayloads(clientPayloads);
                    }
                }));
    }

}
