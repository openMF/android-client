package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Client;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 08/08/16.
 */
public class SyncClientsDialogPresenter extends BasePresenter<SyncClientsDialogMvpView> {

    private final DataManagerClient mDataManagerClient;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SyncClientsDialogPresenter(DataManagerClient dataManagerClient) {
        mDataManagerClient = dataManagerClient;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SyncClientsDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void saveClientInDatabase(Client client) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerClient.saveClientInDatabase(client)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Client>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_sync);
                    }

                    @Override
                    public void onNext(Client client) {
                        getMvpView().showClientSyncedSuccessfully(client);
                    }
                }));
    }

    
}
