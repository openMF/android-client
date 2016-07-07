package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;

import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.OfficeOpeningDate;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Rajan Maurya on 7/7/16.
 */
@Singleton
public class DatabaseHelperOffices {


    @Inject
    public DatabaseHelperOffices() {

    }


    public Observable<Void> saveAllOffices(final List<Office> offices) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                for (Office office : offices) {

                    OfficeOpeningDate officeOpeningDate = new OfficeOpeningDate();
                    officeOpeningDate.setOfficeId(office.getId());
                    officeOpeningDate.setYear(office.getOpeningDate().get(0));
                    officeOpeningDate.setMonth(office.getOpeningDate().get(1));
                    officeOpeningDate.setDay(office.getOpeningDate().get(2));

                    office.setOfficeOpeningDate(officeOpeningDate);
                    office.save();
                }

            }
        });
        return null;
    }


    public Observable<List<Office>> readAllOffices() {
        return Observable.create(new Observable.OnSubscribe<List<Office>>() {
            @Override
            public void call(Subscriber<? super List<Office>> subscriber) {

                List<Office> offices = SQLite.select()
                        .from(Office.class)
                        .queryList();

                for (int i = 0; i < offices.size(); i++) {
                    offices.get(i).setOpeningDate(Arrays.asList(
                            offices.get(i).getOfficeOpeningDate().getYear(),
                            offices.get(i).getOfficeOpeningDate().getMonth(),
                            offices.get(i).getOfficeOpeningDate().getDay()));
                }

                subscriber.onNext(offices);
            }
        });
    }
}
