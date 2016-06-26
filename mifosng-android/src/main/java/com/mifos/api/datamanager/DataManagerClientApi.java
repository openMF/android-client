package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.DatabaseHelper;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

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
public class DataManagerClientApi {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelper mDatabaseHelper;

    @Inject
    public DataManagerClientApi(BaseApiManager baseApiManager,
                                DatabaseHelper databaseHelper) {

        mBaseApiManager = baseApiManager;
        mDatabaseHelper = databaseHelper;
    }


    public Observable<Void> getAllClients() {
        return mBaseApiManager.getClientsApi().getAllClients()
                .concatMap(new Func1<Page<Client>, Observable<? extends Void>>() {
                    @Override
                    public Observable<? extends Void> call(Page<Client> clientPage) {
                        return mDatabaseHelper.saveAllClients(clientPage);
                    }
                });
                //.doOnCompleted()
    }

}
