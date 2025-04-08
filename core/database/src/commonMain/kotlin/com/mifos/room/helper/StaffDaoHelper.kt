/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.helper

import com.mifos.room.dao.StaffDao
import com.mifos.room.entities.organisation.StaffEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Pronay Sarker on 14/02/2025 (2:17 AM)
 */
class StaffDaoHelper(
    private val staffDao: StaffDao,
) {
    suspend fun saveAllStaffOfOffices(staffs: List<StaffEntity>) {
        staffDao.insertStaffs(staffs)
    }

    fun getAllStaffOffices(officeId: Int): Flow<List<StaffEntity>> {
        return staffDao.getAllStaff(officeId)
    }
}
