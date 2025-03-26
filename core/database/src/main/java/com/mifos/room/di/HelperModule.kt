package com.mifos.room.di

import com.mifos.core.common.network.MifosDispatchers
import com.mifos.room.helper.CenterDaoHelper
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val ioDispatcher = named(MifosDispatchers.IO.name)

val HelperModule = module {
    single { CenterDaoHelper(get(), get(ioDispatcher)) }
//    single { Charge }
}