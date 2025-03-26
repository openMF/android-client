package com.mifos.cmp.navigation

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config : KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules()
    }
}