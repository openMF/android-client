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
import com.mifos.core.common.network.MifosDispatchers
import com.mifos.room.MifosDatabase
import com.mifos.room.MifosDatabaseMigrations
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import template.core.base.database.AppDatabaseFactory
import kotlin.coroutines.CoroutineContext

/**
 * Android `actual val platformModule` — provides the [MifosDatabase] singleton via
 * `AppDatabaseFactory(androidApplication())`. Migrations + entity declarations live
 * in the commonMain [MifosDatabase] file; this module is purely Android-specific
 * initialization (Context, driver, dispatcher).
 */
actual val platformModule: Module = module {
    single<MifosDatabase> {
        val ioContext: CoroutineContext = getKoin().get(named(MifosDispatchers.IO.name))

        AppDatabaseFactory(androidApplication())
            .createDatabase(MifosDatabase::class.java, MifosDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigrationOnDowngrade(false)
            .addMigrations(*MifosDatabaseMigrations)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(ioContext)
            .build()
    }
}
