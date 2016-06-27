package com.mifos.mifosxdroid.online.clientlist;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.utils.EspressoIdlingResource;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 6/6/16.
 * This Presenter Holds the All Logic to request to DataManagerClient and DataManagerClient Take
 * care of that From Where Data will come Database or REST API.
 */
public class ClientListPresenter extends BasePresenter<ClientListMvpView> {


    private final DataManagerClient mDataManagerClient;
    private Subscription mSubscription;

    @Inject
    public ClientListPresenter(DataManagerClient dataManagerClient) {
        mDataManagerClient = dataManagerClient;
    }

    @Override
    public void attachView(ClientListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }


    /**
     * @param paged  True Enabling the Pagination of the API
     * @param offset Value give from which position Fetch ClientList
     * @param limit  Maximum size of the Center
     */
    public void loadClients(boolean paged, int offset, int limit) {
        EspressoIdlingResource.increment(); // App is busy until further notice.
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManagerClient.getAllClients(paged, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Client>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showErrorFetchingClients(
                                "There was some error fetching list");
                        EspressoIdlingResource.decrement(); // App is idle.

                    }

                    @Override
                    public void onNext(Page<Client> clientPage) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientList(clientPage);
                        EspressoIdlingResource.decrement(); // App is idle.
                    }
                });
    }

}
