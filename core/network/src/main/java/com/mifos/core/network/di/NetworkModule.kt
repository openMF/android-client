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
    @BaseApiManagerQualifier
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