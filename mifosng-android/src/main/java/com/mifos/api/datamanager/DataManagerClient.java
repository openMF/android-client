package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperClient;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.templates.clients.ClientsTemplate;
import com.mifos.utils.PrefManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
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
     * This Method Request to the DatabaseHelperClient and DatabaseHelperClient Read the All
     * clients from Client_Table and give the response Page of List of Client
     *
     * @return Page of Client List
     */
    public Observable<Page<Client>> getAllDatabaseClients() {
        return mDatabaseHelperClient.readAllClients();
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


    public Observable<Client> syncClientInDatabase(Client client) {
        return mDatabaseHelperClient.saveClient(client);
    }

    /**
     * This Method Checks the User Status and as accordingly call to Database Helper or Client
     * Retrofit Service.
     * If User is Online then this Client DataManager Request to Client Retrofit to make REST API
     * Request for Client Account.
     * If User if Offline Client DataManager request to Client DatabaseHelper to make Transaction
     * for retrieving the Clients Account.
     *
     * @param clientId Client Id
     * @return All Clients Account, Like Savings, Loan etc Accounts.
     */
    public Observable<ClientAccounts> getClientAccounts(final int clientId) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getClientsApi().getClientAccounts(clientId);
            case 1:
                /**
                 * Return Clients from DatabaseHelperClient only one time.
                 */
                return mDatabaseHelperClient.realClientAccounts(clientId);

            default:
                return Observable.just(new ClientAccounts());
        }
    }


    /**
     * This Method Fetching the Client Accounts (Loan, saving, etc Accounts ) from REST API
     * and get the ClientAccounts and then Saving all Accounts into the Database and give the
     * ClientAccount in return.
     *
     * @param clientId Client Id
     * @return ClientAccounts
     */
    public Observable<ClientAccounts> syncClientAccounts(final int clientId) {
        return mBaseApiManager.getClientsApi().getClientAccounts(clientId)
                .concatMap(new Func1<ClientAccounts, Observable<? extends ClientAccounts>>() {
                    @Override
                    public Observable<? extends ClientAccounts> call(ClientAccounts
                                                                             clientAccounts) {
                        return mDatabaseHelperClient.saveClientAccounts(clientAccounts, clientId);
                    }
                });
    }

    /**
     * This Method for removing the Client Image from his profile on server
     * if its response is true the client does not have any profile Image and if
     * response is false then failed to update the client image from server profile.
     * There can any problem during updating the client image like Network error.
     *
     * @param clientId Client ID
     * @return ResponseBody is the Retrofit 2 response
     */
    public Observable<ResponseBody> deleteClientImage(int clientId) {
        return mBaseApiManager.getClientsApi().deleteClientImage(clientId);
    }


    /**
     * This Method will be called when ever user want to update the client profile image.
     * The response of the updating the client image can be true or false its depends upon the
     * network and right choice image file.
     *
     * @param id  Client Id
     * @param file MultipartBody of the Image file
     * @return ResponseBody is the Retrofit 2 response
     */
    public Observable<ResponseBody> uploadClientImage(int id, MultipartBody.Part file) {
        return mBaseApiManager.getClientsApi().uploadClientImage(id, file);
    }


    /**
     * This Method will be called when ever user create the client. if user is in online mode
     * then request goes to the server to get the client template and in response client template
     * saves in table in background and return the Observable.just(clientTemplate) to the presenter
     * and if user in offline mode then we load the client Template from Database.
     *
     * @return ClientTemplate
     */
    public Observable<ClientsTemplate> getClientTemplate() {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getClientsApi().getClientTemplate()
                        .concatMap(new Func1<ClientsTemplate, Observable<? extends
                                ClientsTemplate>>() {
                            @Override
                            public Observable<? extends
                                    ClientsTemplate> call(ClientsTemplate clientsTemplate) {
                                return mDatabaseHelperClient.saveClientTemplate(clientsTemplate);
                            }
                        });

            case 1:
                /**
                 * Return Clients from DatabaseHelperClient only one time.
                 */
                return mDatabaseHelperClient.readClientTemplate();

            default:
                return Observable.just(new ClientsTemplate());
        }

    }


    /**
     * This Method create the client by making directly request to server when User is Online
     * and give response client type is created otherwise give error string.
     * if user if offline mode then client details failed by user is saved into Database directly.
     *
     * @param clientPayload Client details filled by user
     * @return Client
     */
    public Observable<Client> createClient(final ClientPayload clientPayload) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getClientsApi().createClient(clientPayload)
                        .concatMap(new Func1<Client, Observable<? extends Client>>() {
                            @Override
                            public Observable<? extends Client> call(Client client) {
                                return Observable.just(client);
                            }
                        });
            case 1:
                /**
                 * If user is in offline mode and he is making client. client payload will be saved
                 * in Database for future synchronization to sever.
                 */
                return mDatabaseHelperClient.saveClientPayloadToDB(clientPayload);

            default:
                return null;
        }
    }


    /**
     * Loading All Client payload from database to sync to the server
     * @return List<ClientPayload></>
     */
    public Observable<List<ClientPayload>> getAllDatabaseClientPayload() {
        return mDatabaseHelperClient.readAllClientPayload();
    }

    /**
     * This method will called when user is syncing the client created from Database.
     * whenever a client is synced then request goes to Database to delete that client form
     * Database and reload the list from Database and update the list in UI
     * @param id of the clientPayload in Database
     * @return List<ClientPayload></>
     */
    public Observable<List<ClientPayload>> deleteAndUpdatePayloads(int id) {
        return mDatabaseHelperClient.deleteAndUpdatePayloads(id);
    }

    public Observable<ClientPayload> updateClientPayload(ClientPayload clientPayload) {
        return mDatabaseHelperClient.updateDatabaseClientPayload(clientPayload);
    }
}
