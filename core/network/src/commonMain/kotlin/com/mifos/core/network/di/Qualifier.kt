package com.mifos.core.network.di

import org.koin.core.qualifier.named

val MifosClient = named("MifosClient")
val KtorClient = named("KtorClient")
val KtorBaseClient = named("KtorBaseClient")