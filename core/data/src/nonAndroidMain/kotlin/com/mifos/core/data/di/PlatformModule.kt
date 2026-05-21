/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.di

import com.mifos.core.data.infra.TimeZoneMonitor
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * `nonAndroidMain` actual for [com.mifos.core.data.di.platformModule]. Binds the
 * simple `flowOf` [TimeZoneMonitor] impl that all non-Android targets share.
 *
 * Replaces the previous per-platform empty `PlatformModule.<platform>.kt` files in
 * `desktopMain`, `nativeMain`, `jsMain`, `wasmJsMain` — those are deleted in B6c.
 */
actual val platformModule: Module
    get() = module {
        single<TimeZoneMonitor> { TimeZoneMonitorImpl() }
    }
