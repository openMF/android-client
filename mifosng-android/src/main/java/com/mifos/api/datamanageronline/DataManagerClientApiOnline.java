package com.mifos.api.datamanageronline;

import com.mifos.api.BaseApiManager;
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
public class DataManagerClientApiOnline {

    public final BaseApiManager mBaseApiManager;

    @Inject
    public DataManagerClientApiOnline(BaseApiManager baseApiManager) {
        mBaseApiManager = baseApiManager;
    }


    public Observable<Page<Client>> getAllClients() {
        return mBaseApiManager
                .getClientsApi()
                .getAllClients()
                .concatMap(new Func1<Page<Client>, Observable<? extends Page<Client>>>() {
                    @Override
                    public Observable<? extends Page<Client>> call(Page<Client> clientPage) {
                        //Call Offline datamanager to save response in Database using DBHelper
                        return Observable.just(clientPage);
                    }
                });
    }

}
