package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.local.databasehelper.DatabaseHelperStaff
import com.mifos.objects.organisation.Staff
import com.mifos.utils.PrefManager.userStatus
import rx.Observable
import rx.functions.Func1
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 7/7/16.
 */
@Singleton
class DataManagerStaff @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    private val mDatabaseHelperStaff: DatabaseHelperStaff
) {
    /**
     * @param officeId
     * @return
     */
    fun getStaffInOffice(officeId: Int): Observable<List<Staff>> {
        return when (userStatus) {
            false -> mBaseApiManager.staffApi.getStaffForOffice(officeId)
                .concatMap { staffs ->
                    mDatabaseHelperStaff.saveAllStaffOfOffices(staffs)
                    Observable.just(staffs)
                }

            true ->
                /**
                 * return all List of Staffs of Office from DatabaseHelperOffices
                 */
                mDatabaseHelperStaff.readAllStaffOffices(officeId)
        }
    }
}