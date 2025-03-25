package com.mifos.feature.pathTracking.di

import org.koin.dsl.module
import com.mifos.feature.pathTracking.PathTrackingViewModel
import org.koin.core.module.dsl.viewModelOf

val PathTrackingModule = module {
    viewModelOf(::PathTrackingViewModel)
}