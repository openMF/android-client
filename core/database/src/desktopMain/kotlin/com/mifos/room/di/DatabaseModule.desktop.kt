/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mifos.room.MifosDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import template.core.base.database.AppDatabaseFactory

actual val platformModule: Module = module {
    single<MifosDatabase> {
        AppDatabaseFactory()
            .createDatabase<MifosDatabase>(
                databaseName = MifosDatabase.DATABASE_NAME,
            )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .fallbackToDestructiveMigrationOnDowngrade(false)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
