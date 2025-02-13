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

import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.mappers.staffs.StaffMapper
import com.mifos.room.entities.organisation.Staff
import com.mifos.room.helper.StaffDaoHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    private val prefManager: PrefManager,
    private val staffDaoHelper: StaffDaoHelper,
) {
    /**
     * @param officeId
     * @return
     */
    fun getStaffInOffice(officeId: Int): Flow<List<Staff>> {
        return when (prefManager.userStatus) {
            false -> flow {
                baseApiManager.getStaffApi().retrieveAll16(
                    officeId.toLong(),
                    null,
                    null,
                    null,
                )
                    .map(StaffMapper::mapFromEntity)
            }

            /**
             * return all List of Staffs of Office from DatabaseHelperOffices
             */
            true -> staffDaoHelper.getAllStaffOffices(officeId)
        }
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
