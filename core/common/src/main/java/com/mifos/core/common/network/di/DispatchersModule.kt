/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifos.core.common.network.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext.plus

val DispatchersModule = module {
    Module.includes(ioDispatcherModule)
    Module.single<kotlinx.coroutines.CoroutineDispatcher>(named(Enum.name)) { kotlinx.coroutines.Dispatchers.Default }
    Module.single<kotlinx.coroutines.CoroutineDispatcher>(named(Enum.name)) { kotlinx.coroutines.Dispatchers.Unconfined }
    Module.single<kotlinx.coroutines.CoroutineScope>(named("ApplicationScope")) {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.Default)
    }
}

expect val ioDispatcherModule: Module
