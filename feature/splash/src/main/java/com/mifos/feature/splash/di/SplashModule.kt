package com.mifos.feature.splash.di

import org.koin.dsl.module
import com.mifos.feature.splash.splash.SplashScreenViewmodel
import org.koin.core.module.dsl.viewModelOf

val SplashModule = module {
    viewModelOf(::SplashScreenViewmodel)
}