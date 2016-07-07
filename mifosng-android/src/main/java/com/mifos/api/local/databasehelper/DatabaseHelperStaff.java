package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;

import com.mifos.objects.organisation.Staff;
import com.mifos.objects.organisation.Staff_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Rajan Maurya on 7/7/16.
 */
@Singleton
public class DatabaseHelperStaff {


    @Inject
    public DatabaseHelperStaff() {

    }


    public Observable<Void> saveAllStaffOfOffices(final List<Staff> staffs) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                for (Staff staff : staffs) {
                    staff.save();
                }

            }
        });

        return null;
    }


    public Observable<List<Staff>> readAllStaffOffices(final int officeId) {
        return Observable.create(new Observable.OnSubscribe<List<Staff>>() {
            @Override
            public void call(Subscriber<? super List<Staff>> subscriber) {

                List<Staff> staffs = SQLite.select()
                        .from(Staff.class)
                        .where(Staff_Table.officeId.eq(officeId))
                        .queryList();

                subscriber.onNext(staffs);

            }
        });
    }
}
