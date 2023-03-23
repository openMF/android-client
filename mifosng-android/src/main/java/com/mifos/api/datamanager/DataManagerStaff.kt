package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.local.databasehelper.DatabaseHelperStaff
import com.mifos.objects.organisation.Staff
import com.mifos.utils.PrefManager
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Atharv Tare on 23/03/23.
 */
@Singleton
class DataManagerStaff @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    val mDatabaseHelperStaff: DatabaseHelperStaff
) {
    /**
     * @param officeId
     * @return
     */
    fun getStaffInOffice(officeId: Int): Observable<List<Staff>> {
        return when (PrefManager.getUserStatus()) {
            0 -> mBaseApiManager.staffApi.getStaffForOffice(officeId)
                .concatMap { staffs ->
                    mDatabaseHelperStaff.saveAllStaffOfOffices(staffs)
                    Observable.just(staffs)
                }
            1 ->
                /**
                 * return all List of Staffs of Office from DatabaseHelperOffices
                 */
                mDatabaseHelperStaff.readAllStaffOffices(officeId)
            else -> {
                val staffs: List<Staff> = ArrayList()
                Observable.just(staffs)
            }
        }
    }
}