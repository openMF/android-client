package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import javax.inject.Singleton
import javax.inject.Inject
import com.mifos.api.local.databasehelper.DatabaseHelperStaff
import com.mifos.api.mappers.staff.StaffMapper
import com.mifos.objects.organisation.Staff
import org.apache.fineract.client.services.StaffApi
import com.mifos.utils.PrefManager
import rx.Observable
import java.util.ArrayList

/**
 * Created by Rajan Maurya on 7/7/16.
 */
@Singleton
class DataManagerStaff @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    val mDatabaseHelperStaff: DatabaseHelperStaff,
    val sdkBaseApiManager: org.mifos.core.apimanager.BaseApiManager
) {
    private val staffApi: StaffApi
        private get() = sdkBaseApiManager.getStaffApi()

    /**
     * @param officeId
     * @return
     */
    fun getStaffInOffice(officeId: Int): Observable<List<Staff>> {
        return when (PrefManager.getUserStatus()) {
            0 -> {
                staffApi.retrieveAll16(
                    officeId.toLong(), null,
                    null, "all"
                )
                    .map<List<Staff>> { StaffMapper.mapFromEntityList(it) }
                    .concatMap { staffs: List<Staff> ->
                        mDatabaseHelperStaff.saveAllStaffOfOffices(staffs)
                        Observable.just(staffs)
                    }
                /**
                 * return all List of Staffs of Office from DatabaseHelperOffices
                 */
                mDatabaseHelperStaff.readAllStaffOffices(officeId)
            }
            1 -> mDatabaseHelperStaff.readAllStaffOffices(officeId)
            else -> {
                val staffs: List<Staff> = ArrayList()
                Observable.just(staffs)
            }
        }
    }
}