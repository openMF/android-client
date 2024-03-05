package com.mifos.core.databasehelper

import android.os.AsyncTask
import com.mifos.core.objects.organisation.Staff
import com.mifos.core.objects.organisation.Staff_Table
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 7/7/16.
 */
@Singleton
class DatabaseHelperStaff @Inject constructor() {
    fun saveAllStaffOfOffices(staffs: List<Staff>): Observable<Void>? {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            for (staff in staffs) {
                staff.save()
            }
        }
        return null
    }

    fun readAllStaffOffices(officeId: Int): Observable<List<Staff>> {
        return Observable.create { subscriber ->
            val staffs = SQLite.select()
                .from(Staff::class.java)
                .where(Staff_Table.officeId.eq(officeId))
                .queryList()
            subscriber.onNext(staffs)
        }
    }
}