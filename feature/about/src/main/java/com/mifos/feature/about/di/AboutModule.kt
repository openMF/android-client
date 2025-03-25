package com.mifos.feature.about.di

import com.mifos.feature.about.AboutViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val AboutModule = module {
    viewModelOf(::AboutViewModel)
}