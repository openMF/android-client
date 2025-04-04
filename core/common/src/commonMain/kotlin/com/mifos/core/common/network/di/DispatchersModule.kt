/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.common.network.di

import com.mifos.core.common.network.MifosDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val DispatchersModule = module {
    includes(ioDispatcherModule)
    includes(ioContextModule)
    single<CoroutineScope>(named("ApplicationScope")) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    single<CoroutineDispatcher>(named(MifosDispatchers.Unconfined.name)) { Dispatchers.Unconfined }
}

expect val ioDispatcherModule: Module
expect val ioContextModule: Module
