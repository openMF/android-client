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

import com.mifos.room.MifosDatabase
import com.mifos.room.MifosDatabaseMigrations
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import template.core.base.database.AppDatabaseFactory

/**
 * WasmJS `actual val platformModule` — provides the [MifosDatabase] singleton via
 * `AppDatabaseFactory()` with the default SQLite WASM driver and `Dispatchers.Default`
 * (Wasm has no separate IO threadpool). Migrations + entity declarations live in the
 * commonMain [MifosDatabase] file.
 */
actual val platformModule: Module = module {
    single<MifosDatabase> {
        AppDatabaseFactory()
            .createDatabase<MifosDatabase>(MifosDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigrationOnDowngrade(false)
            .addMigrations(*MifosDatabaseMigrations)
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()
    }
}
