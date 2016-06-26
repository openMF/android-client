package com.mifos.api.local.databasehelper;

import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Rajan Maurya on 24/06/16.
 */
@Singleton
public class DatabaseHelperClientApi {

    @Inject
    public DatabaseHelperClientApi() {

    }



    public Observable<Void> saveAllClients(final Page<Client> clientPage) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {

                for (Client client : clientPage.getPageItems()) {
                    Client dbClient = new Client();
                    dbClient.setId(client.getId());
                    dbClient.setAccountNo(client.getAccountNo());
                    dbClient.setActive(client.isActive());
                    dbClient.save();
                }
            }
        });
    }
}
