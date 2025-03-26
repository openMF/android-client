package com.mifos.core.network.di

import com.mifos.core.network.datamanager.DataManagerAuth
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerCharge
import org.koin.dsl.module

/**
 * Created by Pronay Sarker on 26/03/2025 (12:33 PM)
 */
val DataManagerModule = module {
    single { DataManagerAuth(get()) }
    single { DataManagerCenter(get(), get(), get(), get()) }
    single { DataManagerCharge(get(), get(), get()) }
}