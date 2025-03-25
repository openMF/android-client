package com.mifos.feature.activate.di

import com.mifos.feature.activate.ActivateViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ActivateModule = module {
    viewModelOf(::ActivateViewModel)
}