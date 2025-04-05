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

import androidx.room.Room
import androidx.room.RoomDatabase
import com.mifos.core.common.utils.Constants
import com.mifos.room.MifosDatabase
import java.io.File

class MifosDatabaseFactory {
    fun createDatabase(): RoomDatabase.Builder<MifosDatabase> {
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")
        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), "Bookpedia")
            os.contains("mac") -> File(userHome, "Library/Application Support/Bookpedia")
            else -> File(userHome, ".local/share/Bookpedia")
        }

        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }

        val dbFile = File(appDataDir, Constants.DATABASE_NAME)
        return Room.databaseBuilder<MifosDatabase>(
            name = dbFile.absolutePath,
        )
    }
}
