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
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mifos.room.entities.noncore.ColumnValue

@Dao
interface ColumnValueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(columnValue: ColumnValue)

    @Update
    suspend fun update(columnValue: ColumnValue)

    @Delete
    suspend fun delete(columnValue: ColumnValue)

    @Query("SELECT * FROM ColumnValue WHERE id = :id")
    suspend fun getColumnValue(id: Int): ColumnValue?
}
