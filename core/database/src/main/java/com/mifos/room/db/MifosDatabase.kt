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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mifos.room.dao.ColumnValueDao
import com.mifos.room.entities.noncore.ColumnValue
import com.mifos.room.utils.typeconverters.StringListConverter

@Database(
    // [TODO -> add other entities ]
    entities = [ColumnValue::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(StringListConverter::class)
// ( TODO -> add type converters here )

abstract class MifosDatabase : RoomDatabase() {
    abstract fun columnValueDao(): ColumnValueDao

    companion object {

        private const val NAME: String = "Mifos"

        @Volatile
        private var instance: MifosDatabase? = null

        fun getDatabase(context: Context): MifosDatabase {
            return instance ?: synchronized(this) {
                val currentInstance = Room.databaseBuilder(
                    context.applicationContext,
                    MifosDatabase::class.java,
                    NAME,
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                    )
                    .build()
                instance = currentInstance

                currentInstance
            }
        }
    }
}
