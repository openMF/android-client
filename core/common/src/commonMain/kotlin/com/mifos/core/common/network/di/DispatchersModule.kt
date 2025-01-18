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

import com.mifos.core.common.network.Dispatcher
import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.common.network.MifosDispatchers.Default
import com.mifos.core.common.network.MifosDispatchers.IO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule_s {
    @Provides
    @Dispatcher(IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

// TODO recheck
val DispatchersModule = module {
    includes(ioDispatcherModule)
    single<CoroutineDispatcher>(named(MifosDispatchers.Default.name )) { Dispatchers.Default }
    single<CoroutineDispatcher>(named(MifosDispatchers.IO.name)) { Dispatchers.Unconfined }
//    single<CoroutineScope>(named("ApplicationScope")) {
//        CoroutineScope(SupervisorJob() + Dispatchers.Default)
//    }
}

expect val ioDispatcherModule: Module