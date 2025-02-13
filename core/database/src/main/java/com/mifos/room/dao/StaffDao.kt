/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mifos.room.entities.organisation.Staff
import kotlinx.coroutines.flow.Flow

/**
 * Created by Pronay Sarker on 14/02/2025 (1:54 AM)
 */
@Dao
interface StaffDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffs(staffs: List<Staff>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: Staff)

    @Query("SELECT * FROM Staff WHERE officeId = :officeId")
    fun getAllStaff(officeId: Int): Flow<List<Staff>>
}
