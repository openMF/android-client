package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperClient;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.utils.PrefManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * This DataManager is for Managing Client API, In which Request is going to Server
 * and In Response, We are getting Client API Observable Response using Retrofit2 .
 * Created by Rajan Maurya on 24/06/16.
 */
@Singleton
public class DataManagerClient {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperClient mDatabaseHelperClient;

    @Inject
    public DataManagerClient(BaseApiManager baseApiManager,
                             DatabaseHelperClient databaseHelperClient) {

        mBaseApiManager = baseApiManager;
        mDatabaseHelperClient = databaseHelperClient;
    }


    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get list of the clients. The response is pass to the DatabaseHelperClient
     * that save the response in Database different thread and next pass the response to
     * Presenter to show in the view
     * <p/>
     * If the offset is zero and UserStatus is 1 then fetch all clients list and show on the view.
     * else if offset is not zero and UserStatus is 1 then return default empty response to
     * presenter
     *
     * @param paged  True Enable the Pagination of the client list REST API
     * @param offset Value give from which position Fetch ClientList
     * @param limit  Maximum Number of clients will come in response
     * @return Client List from offset to max Limit
     */
    public Observable<Page<Client>> getAllClients(boolean paged, int offset, int limit) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getClientsApi().getAllClients(paged, offset, limit)
                        .concatMap(new Func1<Page<Client>, Observable<? extends Page<Client>>>() {
                            @Override
                            public Observable<? extends Page<Client>> call(Page<Client>
                                                                                   clientPage) {
                                //Saving Clients in Database
                                mDatabaseHelperClient.saveAllClients(clientPage);
                                return Observable.just(clientPage);
                            }
                        });
            case 1:
                /**
                 * Return All Clients List from DatabaseHelperClient only one time.
                 * If offset is zero this means this is first request and
                 * return all clients from DatabaseHelperClient
                 */
                if (offset == 0)
                    return mDatabaseHelperClient.readAllClients();

            default:
                return Observable.just(new Page<Client>());
        }
    }


    /**
     * This Method
     *
     * @param clientId for Query in database or REST API request.
     * @return The Client Details
     */
    public Observable<Client> getClient(int clientId) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getClientsApi().getClient(clientId)
                        .concatMap(new Func1<Client, Observable<? extends Client>>() {
                            @Override
                            public Observable<? extends Client> call(Client client) {
                                return Observable.just(client);
                            }
                        });
            case 1:
                /**
                 * Return Clients from DatabaseHelperClient only one time.
                 */
                return mDatabaseHelperClient.getClient(clientId);

            default:
                return Observable.just(new Client());
        }
    }


    /**
     * This Method Checks the User Status and as accordingly call to Database Helper or Client
     * Retrofit Service.
     * If User is Online then this Client DataManager Request to Client Retrofit to make REST API
     * Request for Client Account and after service request completion DataManager call to
     * DatabaseHelper to save in to Database.
     * If User if Offline Client DataManager request to Client DatabaseHelper to make Transaction
     * for retrieving the Clients Account.
     *
     * @param clientId Client Id
     * @return All Clients Account, Like Savings, Loan etc Accounts.
     */
    public Observable<ClientAccounts> getClientAccounts(final int clientId) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getClientsApi().getClientAccounts(clientId)
                        .concatMap(new Func1<ClientAccounts, Observable<? extends
                                ClientAccounts>>() {
                            @Override
                            public Observable<? extends ClientAccounts> call(
                                    ClientAccounts clientAccounts) {
                                mDatabaseHelperClient.saveClientAccounts(clientAccounts, clientId);
                                return Observable.just(clientAccounts);
                            }
                        });
            case 1:
                /**
                 * Return Clients from DatabaseHelperClient only one time.
                 */
                return mDatabaseHelperClient.realClientAccounts(clientId);

            default:
                return Observable.just(new ClientAccounts());
        }
    }

}
