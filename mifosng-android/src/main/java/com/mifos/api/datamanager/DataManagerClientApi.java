package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.DatabaseHelper;
import com.mifos.api.local.databasehelper.DatabaseHelperClientApi;
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
    public static DatabaseHelperClientApi mDatabaseHelperClientApi;

    @Inject
    public DataManagerClientApi(BaseApiManager baseApiManager,
                                DatabaseHelper databaseHelper,
                                DatabaseHelperClientApi databaseHelperClientApi) {

        mBaseApiManager = baseApiManager;
        mDatabaseHelper = databaseHelper;
        mDatabaseHelperClientApi = databaseHelperClientApi;
    }


    public Observable<Page<Client>> getAllClients() {
        return mBaseApiManager.getClientsApi().getAllClients()
                .concatMap(new Func1<Page<Client>, Observable<? extends Page<Client>>>() {
                    @Override
                    public Observable<? extends Page<Client>> call(Page<Client> clientPage) {
                        //Saving Clients in Database
                        mDatabaseHelper.saveAllClients(clientPage);
                        return Observable.just(clientPage);
                    }
                });

    }

}
