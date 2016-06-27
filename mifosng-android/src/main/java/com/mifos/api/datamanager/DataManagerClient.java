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


    public Observable<Page<Client>> getAllClients() {

        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getClientsApi().getAllClients()
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
                //Return DatabaseHelper saved clients
                return mDatabaseHelperClient.readAllClients();

            default:
                return null;
        }

    }


    public Observable<Page<Client>> getAllClients(int offset, int limit) {
        return mBaseApiManager.getClientsApi().getAllClients(offset, limit)
                .concatMap(new Func1<Page<Client>, Observable<? extends Page<Client>>>() {
                    @Override
                    public Observable<? extends Page<Client>> call(Page<Client>
                                                                           clientPage) {
                        //Saving Clients in Database
                        mDatabaseHelperClient.saveAllClients(clientPage);
                        return Observable.just(clientPage);
                    }
                });


    }

}
