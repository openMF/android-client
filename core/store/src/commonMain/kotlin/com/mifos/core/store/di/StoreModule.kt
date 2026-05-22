/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.store.di

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module for app-level Store wiring. Feature modules add their `Store<K, V>`
 * registrations here, qualifier-bound via [com.mifos.core.store.AppStoreRegistry].
 */
val appStoreModule: Module = module {
    // single(qualifier = AppStoreRegistry.Foo) { FooStore(get(), get()) }
}
