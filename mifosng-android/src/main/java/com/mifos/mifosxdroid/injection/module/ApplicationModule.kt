package com.mifos.mifosxdroid.injection.module

import com.mifos.api.BaseApiManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author Rajan Maurya
 * Provide application-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideBaseApiManager(): BaseApiManager {
        return BaseApiManager()
    }

}