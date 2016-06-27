package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 24/06/16.
 */
@Singleton
public class DatabaseHelperClient {

    @Inject
    public DatabaseHelperClient() {

    }


    @Nullable
    public Observable<Void> saveAllClients(final Page<Client> clientPage) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                for (Client client : clientPage.getPageItems()) {
                    client.save();
                }
            }
        });
        return null;
    }

    //TODO Implement Observable to load the Client List
    public Observable<Page<Client>> readAllClients() {

        List<Client> clients = SQLite.select()
                .from(Client.class)
                .queryList();
        Page<Client> clientPage = new Page<>();
        clientPage.setPageItems(clients);

        return Observable.just(clientPage);
    }
}
