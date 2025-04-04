/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.datastore.di

import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.datastore.UserPreferencesDataSource
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.UserPreferencesRepositoryImpl
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val PreferencesModule = module {
    factory<Settings> { Settings() }

    factory {
        UserPreferencesDataSource(
            settings = get(),
            dispatcher = get(named(MifosDispatchers.IO.name)),
        )
    }

    singleOf(::UserPreferencesRepositoryImpl) bind UserPreferencesRepository::class
}
