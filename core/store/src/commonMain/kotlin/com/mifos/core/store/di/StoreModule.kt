/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package com.mifos.core.store.di

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module for app-level Store wiring.
 *
 * Phase C feature waves register their `Store` instances here, qualifier-bound via
 * [com.mifos.core.store.AppStoreRegistry]. Empty in this Phase B scaffold — the seam
 * exists so feature modules have one obvious DI module to extend without modifying
 * `core-base/store`.
 *
 * Wire into the Koin start-up (Phase B follow-up — `cmp-android` / `cmp-ios` app modules):
 * ```kotlin
 * startKoin {
 *     modules(appStoreModule, /* ...other modules */)
 * }
 * ```
 */
val appStoreModule: Module = module {
    // Phase C feature-wave registrations land here, e.g.
    //   single(qualifier = AppStoreRegistry.ClientList) { ClientListStore(get(), get()) }
    //   single(qualifier = AppStoreRegistry.LoanAccounts) { LoanAccountsStore(get(), get()) }
}
