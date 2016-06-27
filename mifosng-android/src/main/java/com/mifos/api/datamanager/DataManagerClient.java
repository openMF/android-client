package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperClient;
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

}
