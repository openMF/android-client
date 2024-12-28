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

import com.mifos.core.dbobjects.organisation.Staff
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.mappers.staffs.StaffMapper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 7/7/16.
 */
@Singleton
class DataManagerStaff @Inject constructor(
    val mBaseApiManager: BaseApiManager,
//    private val mDatabaseHelperStaff: DatabaseHelperStaff,
    private val baseApiManager: org.mifos.core.apimanager.BaseApiManager,
//    private val prefManager: com.mifos.core.datastore.PrefManager,
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
