package com.mifos.mifosxdroid.online.clientlist;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.base.Presenter;
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
 */
public class ClientListPresenter implements Presenter<ClientListMvpView> {


    private final DataManagerClient mDataManagerClient;
    private Subscription mSubscription;
    private ClientListMvpView mClientListMvpView;

    @Inject
    public ClientListPresenter(DataManagerClient dataManagerClient) {
        mDataManagerClient = dataManagerClient;
    }

    @Override
    public void attachView(ClientListMvpView mvpView) {
        mClientListMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mClientListMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }


    public void loadClients() {
        EspressoIdlingResource.increment(); // App is busy until further notice.
        mClientListMvpView.showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManagerClient.getAllClients()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Client>>() {
                    @Override
                    public void onCompleted() {
                        mClientListMvpView.showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientListMvpView.showProgressbar(false);
                        mClientListMvpView.showErrorFetchingClients(
                                "There was some error fetching list");
                        EspressoIdlingResource.decrement(); // App is idle.

                    }

                    @Override
                    public void onNext(Page<Client> clientPage) {
                        mClientListMvpView.showProgressbar(false);
                        mClientListMvpView.showClientList(clientPage);
                        EspressoIdlingResource.decrement(); // App is idle.
                    }
                });
    }

    public void loadMoreClients(int offset, int limit) {
        mClientListMvpView.showSwipeRefreshLayout(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManagerClient.getAllClients(offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Client>>() {
                    @Override
                    public void onCompleted() {
                        mClientListMvpView.showSwipeRefreshLayout(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mClientListMvpView.showSwipeRefreshLayout(false);
                        mClientListMvpView.showErrorFetchingClients(
                                "There was some error fetching list");

                    }

                    @Override
                    public void onNext(Page<Client> clientPage) {
                        mClientListMvpView.showSwipeRefreshLayout(false);
                        mClientListMvpView.showMoreClientsList(clientPage);
                    }
                });
    }
}
