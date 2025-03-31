package com.mifos.core.common.network.di

import com.mifos.core.common.network.MifosDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val ioDispatcherModule: Module
    get() = module {
        single<CoroutineDispatcher>(named(MifosDispatchers.IO.name)) { Dispatchers.Default }
    }