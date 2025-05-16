package com.mifos.core.data.di

import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.data.util.ConnectivityManagerNetworkMonitor
import com.mifos.core.data.util.NetworkMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mifos.mobile.core.data.di.AndroidPlatformDependentDataModule


val AndroidDataModule = module {
    single<NetworkMonitor> {
        ConnectivityManagerNetworkMonitor(androidContext(), get(named(MifosDispatchers.IO.name)))
    }

    single {
        AndroidPlatformDependentDataModule(
            context = androidContext(),
            dispatcher = get(named(MifosDispatchers.IO.name)),
        )
    }
}

actual val platformModule: Module= AndroidDataModule

actual val getPlatformDataModule: PlatformDependentDataModule
    get() = org.koin.core.context.GlobalContext.get().get()