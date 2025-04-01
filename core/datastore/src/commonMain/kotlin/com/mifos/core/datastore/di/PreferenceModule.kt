package com.mifos.core.datastore.di

import com.mifos.core.datastore.UserPreferencesDataSource
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.UserPreferencesRepositoryImpl
import com.russhwolf.settings.Settings
import org.koin.core.qualifier.named
import org.koin.dsl.module

val PreferencesModule = module {
    factory<Settings> { Settings() }

    factory {
        UserPreferencesDataSource(
            settings = get(),
            dispatcher = get(named(MifosDispatchers.IO.name)),
        )
    }

    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(
            preferenceManager = get(),
            ioDispatcher = get(named(MifosDispatchers.IO.name)),
            unconfinedDispatcher = get(named(MifosDispatchers.Unconfined.name)),
        )
    }
}

// Should be removed after common module conversion
enum class MifosDispatchers {
    Default,
    IO,
    Unconfined,
}