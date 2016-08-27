package com.mifos.mifosxdroid.online.clientlist;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.utils.EspressoIdlingResource;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 6/6/16.
 * This Presenter Holds the All Logic to request to DataManagerClient and DataManagerClient Take
 * care of that From Where Data will come Database or REST API.
 */
public class ClientListPresenter extends BasePresenter<ClientListMvpView> {


    private final DataManagerClient mDataManagerClient;
    private CompositeSubscription mSubscriptions;

    private Page<Client> databaseClientPage;

    private int limit = 100;
    private Boolean loadmore = false;

    @Inject
    public ClientListPresenter(DataManagerClient dataManagerClient) {
        mDataManagerClient = dataManagerClient;
        mSubscriptions = new CompositeSubscription();
        databaseClientPage = new Page<>();
    }

    @Override
    public void attachView(ClientListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadClients(Boolean loadmore, int offset) {
        this.loadmore = loadmore;
        loadClients(true, offset, limit);
    }

    public void showClientList(List<Client> clients) {
        if (loadmore) {
            getMvpView().showLoadMoreClients(clients);
        } else {
            getMvpView().showClientList(clients);
        }
    }

    //This method handling Group Client event
    public void showGroupClients(List<Client> clients) {
        if (clients.size() == 0) {
            getMvpView().showEmptyClientList(R.string.empty_group_clients);
        } else {
            getMvpView().showGroupClients(clients);
        }
        getMvpView().unregisterSwipeAndScrollListener();
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
        mSubscriptions.add(mDataManagerClient.getAllClients(paged, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Client>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError();
                        EspressoIdlingResource.decrement(); // App is idle.

                    }

                    @Override
                    public void onNext(Page<Client> clientPage) {

                        getMvpView().showProgressbar(false);
                        List<Client> clients = clientPage.getPageItems();

                        if (clients.size() == 0 && !loadmore) {
                            getMvpView().showEmptyClientList(R.string.empty_client_list);
                            getMvpView().unregisterSwipeAndScrollListener();
                        } else if (clients.size() == 0 && loadmore) {
                            getMvpView().showMessage(R.string.no_more_clients_available);
                        } else {
                            showClientList(clients);
                        }
                        getMvpView().showClientList(checkClientAlreadySyncedOrNot(clientPage.getPageItems()));

                        EspressoIdlingResource.decrement(); // App is idle.
                    }
                }));
    }


    /**
     * This Method Loading the Client From Database. It request Observable to DataManagerClient
     * and DataManagerClient Request to DatabaseHelperClient to load the Client List Page from the
     * Client_Table and As the Client List Page is loaded DataManagerClient gives the Client List
     * Page after getting response from DatabaseHelperClient
     */
    public void loadDatabaseClients() {
        checkViewAttached();
        mSubscriptions.add(mDataManagerClient.getAllDatabaseClients()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Client>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(Page<Client> clientPage) {
                        databaseClientPage = clientPage;
                    }
                })
        );
    }


    /**
     * This Method Filtering the Clients Loaded from Server is already sync or not. If yes the
     * put the client.setSync(true) and view will show those clients as sync already to user
     *
     * @param
     * @return Page<Client>
     */
    public List<Client> checkClientAlreadySyncedOrNot(final List<Client> clients) {
        if (databaseClientPage.getPageItems().size() != 0) {

            for (int i = 0; i < databaseClientPage.getPageItems().size(); ++i) {
                for (int j = 0; j < clients.size(); ++j) {
                    if (databaseClientPage.getPageItems().get(i).getId() ==
                            clients.get(j).getId()) {

                        clients.get(j).setSync(true);
                        break;
                    }
                }
            }
        }
        return clients;
    }

}
