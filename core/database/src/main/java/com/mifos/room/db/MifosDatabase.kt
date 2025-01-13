/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mifos.room.dao.ColumnValueDao
import com.mifos.room.entities.noncore.ColumnValue
import com.mifos.room.utils.typeconverters.ListTypeConverters

@Database(
    // [TODO -> add other entities ]
    entities = [ColumnValue::class],
    version = MifosDatabase.VERSION,
    exportSchema = true,
    autoMigrations = [],
)
@TypeConverters(ListTypeConverters::class)
// ( TODO -> add type converters here )

abstract class MifosDatabase : RoomDatabase() {
    abstract fun columnValueDao(): ColumnValueDao

    companion object {
        const val VERSION = 1
    }
}
