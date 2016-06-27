package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

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

    //TODO Implement Observable Transaction to load Client List
    public Observable<Page<Client>> readAllClients() {

        return Observable.create(new Observable.OnSubscribe<Page<Client>>() {
            @Override
            public void call(Subscriber<? super Page<Client>> subscriber) {

                Page<Client> clientPage = new Page<>();
                clientPage.setPageItems(SQLite.select()
                        .from(Client.class)
                        .queryList());
                subscriber.onNext(clientPage);
                subscriber.onCompleted();
            }
        });

    }
}
