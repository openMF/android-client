package com.mifos.core.databasehelper

import android.os.AsyncTask
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.OfficeOpeningDate
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 7/7/16.
 */
@Singleton
class DatabaseHelperOffices @Inject constructor() {
    fun saveAllOffices(offices: List<Office>): Observable<Void>? {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            for (office in offices) {
                val officeOpeningDate = OfficeOpeningDate()
                officeOpeningDate.officeId = office.id
                officeOpeningDate.year = office.openingDate[0]
                officeOpeningDate.month = office.openingDate[1]
                officeOpeningDate.day = office.openingDate[2]
                office.officeOpeningDate = officeOpeningDate
                office.save()
            }
        }
        return null
    }

    fun readAllOffices(): Observable<List<Office>> {
        return Observable.create<List<Office>> { subscriber ->
            val offices = SQLite.select()
                .from(Office::class.java)
                .queryList()
            for (i in offices.indices) {
                offices[i].openingDate = listOf(
                    offices[i].officeOpeningDate?.year,
                    offices[i].officeOpeningDate?.month,
                    offices[i].officeOpeningDate?.day
                )
            }
            subscriber.onNext(offices)
        }
    }
}