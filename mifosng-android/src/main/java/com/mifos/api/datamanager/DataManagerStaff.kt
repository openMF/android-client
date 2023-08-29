package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.local.databasehelper.DatabaseHelperStaff
import com.mifos.mappers.staffs.StaffMapper
import com.mifos.objects.organisation.Staff
import com.mifos.utils.PrefManager.userStatus
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 7/7/16.
 */
@Singleton
class DataManagerStaff @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    private val mDatabaseHelperStaff: DatabaseHelperStaff,
    private val baseApiManager: org.mifos.core.apimanager.BaseApiManager
) {
    /**
     * @param officeId
     * @return
     */
    fun getStaffInOffice(officeId: Int): Observable<List<Staff>> {
        return when (userStatus) {
            false -> baseApiManager.getStaffApi().retrieveAll16(officeId.toLong(), null, null, null)
                .map(StaffMapper::mapFromEntityList)

            true ->
                /**
                 * return all List of Staffs of Office from DatabaseHelperOffices
                 */
                mDatabaseHelperStaff.readAllStaffOffices(officeId)
        }
    }
}