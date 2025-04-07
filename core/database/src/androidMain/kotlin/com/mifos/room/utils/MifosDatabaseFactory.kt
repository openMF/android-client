/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.utils

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mifos.core.common.utils.Constants
import com.mifos.room.MifosDatabase

class MifosDatabaseFactory(
    private val context: Context,
) {
    fun createDatabase(): RoomDatabase.Builder<MifosDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(Constants.DATABASE_NAME)

        return Room.databaseBuilder<MifosDatabase>(
            context = appContext,
            name = dbFile.absolutePath,
        )
    }
}
