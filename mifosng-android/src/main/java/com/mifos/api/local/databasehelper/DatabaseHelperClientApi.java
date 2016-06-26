package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;

import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 24/06/16.
 */
@Singleton
public class DatabaseHelperClientApi {

    @Inject
    public DatabaseHelperClientApi() {

    }



    public Observable<Page<Client>> saveAllClients(final Page<Client> clientPage) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                for (Client client : clientPage.getPageItems()) {
                    client.save();
                }
            }
        });
        return Observable.just(clientPage);

    }
}
