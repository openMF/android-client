package com.mifos.feature.search.di

import org.koin.dsl.module
import com.mifos.feature.search.SearchViewModel
import org.koin.core.module.dsl.viewModelOf

val SearchModule = module {
    viewModelOf(::SearchViewModel)
}