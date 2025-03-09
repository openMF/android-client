/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

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
) {
    /**
     * @param officeId
     * @return
     */
    suspend fun getStaffInOffice(officeId: Int): List<Staff> {
        return mBaseApiManager.staffApi.getAllStaff(officeId = officeId.toLong())
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
