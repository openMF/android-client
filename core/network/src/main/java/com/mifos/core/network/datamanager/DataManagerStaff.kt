package com.mifos.core.network.datamanager

import com.mifos.core.databasehelper.DatabaseHelperStaff
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.mappers.staffs.StaffMapper
import com.mifos.core.objects.organisation.Staff
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
    private val prefManager: com.mifos.core.datastore.PrefManager
) {
    /**
     * @param officeId
     * @return
     */
    suspend fun getStaffInOffice(officeId: Int): List<Staff> {
        return baseApiManager.getStaffApi().retrieveAll16(officeId.toLong(), null, null, null)
            .map(StaffMapper::mapFromEntity)
    }
//    fun getStaffInOffice(officeId: Int): Observable<List<Staff>> {
//        return when (prefManager.userStatus) {
//            false -> baseApiManager.getStaffApi().retrieveAll16(officeId.toLong(), null, null, null)
//                .map(StaffMapper::mapFromEntityList)
//
//            true ->
//                /**
//                 * return all List of Staffs of Office from DatabaseHelperOffices
//                 */
//                mDatabaseHelperStaff.readAllStaffOffices(officeId)
//        }
//    }
}