package com.mifos.mifosxdroid.offline.syncclientpayloads;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.ClientPayload;
import com.mifos.utils.MFErrorParser;

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
                        getMvpView().showError(R.string.failed_to_load_clientpayload);
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
                        getMvpView().showClientSyncFailed(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(Client client) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSyncResponse();
                    }
                }));
    }


    public void deleteAndUpdateClientPayload(int id, long clientCreationTIme) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.deleteAndUpdatePayloads(id, clientCreationTIme)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<ClientPayload>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_update_list);
                    }

                    @Override
                    public void onNext(List<ClientPayload> clientPayloads) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showPayloadDeletedAndUpdatePayloads(clientPayloads);
                    }
                }));
    }

    public void updateClientPayload(ClientPayload clientPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerClient.updateClientPayload(clientPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientPayload>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_update_list);
                    }

                    @Override
                    public void onNext(ClientPayload clientPayload) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientPayloadUpdated(clientPayload);
                    }
                })
        );
    }

}
