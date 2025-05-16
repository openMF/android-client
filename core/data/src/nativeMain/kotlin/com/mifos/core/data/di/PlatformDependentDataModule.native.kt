package com.mifos.core.data.di

import org.koin.core.module.Module
import org.koin.dsl.module

actual val getPlatformDataModule: PlatformDependentDataModule
    get() = NativePlatformDependentDataModule()

actual val platformModule: Module
    get() = module {
        single<PlatformDependentDataModule> { getPlatformDataModule }
    }
