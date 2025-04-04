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

import androidx.room.Room
import com.mifos.core.common.network.MifosDispatchers
import com.mifos.room.db.MifosDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

val DatabaseModule = module {
    single {
        val ioContext: CoroutineContext = getKoin().get(named(MifosDispatchers.IO.name))

        Room.databaseBuilder(
            context = androidContext(),
            klass = MifosDatabase::class.java,
            name = "mifos-database",
        ).enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration(true)
            .setQueryCoroutineContext(ioContext)
            .build()
    }
}
