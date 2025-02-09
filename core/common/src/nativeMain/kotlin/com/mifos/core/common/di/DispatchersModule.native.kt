package com.mifos.core.common.di

import com.mifos.core.common.network.MifosDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.qualifier.named
import kotlinx.coroutines.Dispatchers


actual val ioDispatcherModule: Module
    get() = module {
        single<CoroutineDispatcher>(named(MifosDispatchers.IO.name)) { Dispatchers.Default }
    }