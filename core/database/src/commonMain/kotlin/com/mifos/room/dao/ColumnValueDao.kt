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

import com.mifos.room.entities.noncore.ColumnValue
import com.mifos.room.utils.Dao
import com.mifos.room.utils.Delete
import com.mifos.room.utils.Insert
import com.mifos.room.utils.OnConflictStrategy
import com.mifos.room.utils.Query
import com.mifos.room.utils.Update

@Dao
interface ColumnValueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ColumnValue::class)
    suspend fun insert(columnValue: ColumnValue)

    @Update(entity = ColumnValue::class)
    suspend fun update(columnValue: ColumnValue)

    @Delete(entity = ColumnValue::class)
    suspend fun delete(columnValue: ColumnValue)

    @Query("SELECT * FROM ColumnValue WHERE id = :id")
    suspend fun getColumnValue(id: Int): ColumnValue?
}
