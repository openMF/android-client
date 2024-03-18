package com.mifos.core.network.di

import com.mifos.core.datastore.PrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifos.core.apimanager.BaseApiManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseApiManager(prefManager: PrefManager): com.mifos.core.network.BaseApiManager {
        return com.mifos.core.network.BaseApiManager(prefManager)
    }

    @Provides
    @Singleton
    fun provideSdkBaseApiManager(prefManager: PrefManager): BaseApiManager {
        val usernamePassword: Pair<String, String> = prefManager.usernamePassword
        val baseManager = BaseApiManager.getInstance()
        baseManager.createService(
            usernamePassword.first,
            usernamePassword.second,
            prefManager.getInstanceUrl(),
            prefManager.getTenant(),
            false
        )
        return baseManager
    }
}