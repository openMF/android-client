/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mifos.core.common.network.MifosDispatchers
import com.mifos.room.MifosDatabase
import com.mifos.room.utils.MifosDatabaseFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

actual val PlatformSpecificDatabaseModule: Module = module {
    single<MifosDatabase> {
        val ioContext: CoroutineContext = getKoin().get(named(MifosDispatchers.IO.name))

        MifosDatabaseFactory()
            .createDatabase()
            .fallbackToDestructiveMigrationOnDowngrade(false)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(ioContext)
            .build()
    }
}
