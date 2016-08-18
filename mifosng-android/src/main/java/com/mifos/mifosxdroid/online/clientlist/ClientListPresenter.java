package com.mifos.mifosxdroid.online.clientlist;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.utils.EspressoIdlingResource;

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
                        getMvpView().showErrorFetchingClients();
                        EspressoIdlingResource.decrement(); // App is idle.

                    }

                    @Override
                    public void onNext(Page<Client> clientPage) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showClientList(filterClientsAndShowClientsList(clientPage));

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
                        getMvpView().showErrorFetchingClients();
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
     * @param clientPage Client List Page
     * @return Page<Client>
     */
    public Page<Client> filterClientsAndShowClientsList(final Page<Client> clientPage) {
        if (databaseClientPage.getPageItems().size() != 0) {

            for (int i = 0; i < databaseClientPage.getPageItems().size(); ++i) {
                for (int j = 0; j < clientPage.getPageItems().size(); ++j) {
                    if (databaseClientPage.getPageItems().get(i).getId() ==
                            clientPage.getPageItems().get(j).getId()) {

                        clientPage.getPageItems().get(j).setSync(true);
                        break;
                    }
                }
            }
        }
        return clientPage;
    }

}
