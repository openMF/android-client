package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;

import com.mifos.objects.organisation.Staff;
import com.mifos.objects.organisation.Staff_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;


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
        return Observable.defer(new Callable<ObservableSource<? extends List<Staff>>>() {
            @Override
            public ObservableSource<? extends List<Staff>> call() throws Exception {
                List<Staff> staffs = SQLite.select()
                        .from(Staff.class)
                        .where(Staff_Table.officeId.eq(officeId))
                        .queryList();

                return Observable.just(staffs);
            }
        });
    }
}
