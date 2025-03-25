package com.mifos.feature.auth.di

import com.mifos.feature.auth.login.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val AuthModule = module {
    viewModelOf(::LoginViewModel)
}