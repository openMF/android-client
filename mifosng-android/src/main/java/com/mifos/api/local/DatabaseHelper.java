package com.mifos.api.local;

import com.mifos.api.local.databasehelper.DatabaseHelperClientApi;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 23/06/16.
 */
@Singleton
public class DatabaseHelper {

    public final DatabaseHelperClientApi mDatabaseHelperClientApi;

    @Inject
    public DatabaseHelper(DatabaseHelperClientApi databaseHelperClientApi) {
        mDatabaseHelperClientApi = databaseHelperClientApi;
    }


    /**
     * Saving List of Clients in Database
     *
     * @param clientPage Client List for saving in Database
     */
    public Observable<Void> saveAllClients(final Page<Client> clientPage) {
        return mDatabaseHelperClientApi.saveAllClients(clientPage);

    }

}
