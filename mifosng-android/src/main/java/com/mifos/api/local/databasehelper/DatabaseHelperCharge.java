package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;

import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 4/7/16.
 */
@Singleton
public class DatabaseHelperCharge {

    @Inject
    public DatabaseHelperCharge() {

    }



    public Observable<Void> saveClientCharges(final Page<Charges> chargesPage, final int clientId) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                for (Charges charges : chargesPage.getPageItems()) {
                    charges.setClientId(clientId);
                    charges.save();
                }
            }
        });
        return null;
    }

}
