package com.mifos.core.network.datamanager

import com.mifos.core.databasehelper.DatabaseHelperStaff
import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.mappers.staffs.StaffMapper
import com.mifos.core.objects.organisation.Staff
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
    private val baseApiManager: org.mifos.core.apimanager.BaseApiManager,
    private val prefManager: PrefManager
) {
    /**
     * @param officeId
     * @return
     */
    fun getStaffInOffice(officeId: Int): Observable<List<Staff>> {
        return when (prefManager.userStatus) {
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