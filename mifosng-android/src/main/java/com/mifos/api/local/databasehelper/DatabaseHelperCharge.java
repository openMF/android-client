package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;

import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.loan.LoanAccount_Table;
import com.mifos.objects.accounts.savings.Charge;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Charges_Table;
import com.mifos.objects.client.ClientDate;
import com.mifos.objects.client.Page;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Rajan Maurya on 4/7/16.
 */
@Singleton
public class DatabaseHelperCharge {

    @Inject
    public DatabaseHelperCharge() {

    }


    /**
     *
     * @param chargesPage
     * @param clientId
     * @return
     */
    public Observable<Void> saveClientCharges(final Page<Charges> chargesPage, final int clientId) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                for (Charges charges : chargesPage.getPageItems()) {
                    charges.setClientId(clientId);

                    ClientDate clientDate = new ClientDate(0,charges.getId(),
                            charges.getDueDate().get(2),
                            charges.getDueDate().get(1),
                            charges.getDueDate().get(0));
                    charges.setChargeDueDate(clientDate);
                    charges.save();
                }
            }
        });
        return null;
    }


    public Observable<Page<Charges>> readClientCharges(final int clientId) {
        return Observable.create(new Observable.OnSubscribe<Page<Charges>>() {
            @Override
            public void call(Subscriber<? super Page<Charges>> subscriber) {

                List<Charges> chargesList = SQLite.select()
                        .from(Charges.class)
                        .where(Charges_Table.clientId.eq(clientId))
                        .queryList();

                for (int i= 0 ; i < chargesList.size() ; i++) {
                    chargesList.get(i).setDueDate(Arrays.asList(
                            chargesList.get(i).getChargeDueDate().getYear(),
                            chargesList.get(i).getChargeDueDate().getMonth(),
                            chargesList.get(i).getChargeDueDate().getDay()));
                }

                Page<Charges> chargePage = new Page<Charges>();
                chargePage.setPageItems(chargesList);
                subscriber.onNext(chargePage);


                /*for (Charges charges : chargesList) {

                    charges.setDueDate(Arrays.asList(charges.getChargeDueDate().getYear(),charges
                            .getChargeDueDate().getMonth(), charges.getChargeDueDate().getDay()));
                }*/

            }
        });

    }

}
