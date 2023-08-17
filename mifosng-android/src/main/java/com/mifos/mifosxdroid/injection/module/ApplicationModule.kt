package com.mifos.mifosxdroid.injection.module

import android.content.Context
import com.mifos.utils.NetworkUtilsWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.mifos.core.apimanager.BaseApiManager
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
    fun provideBaseApiManager(): com.mifos.api.BaseApiManager {
        return com.mifos.api.BaseApiManager()
    }

    @Provides
    @Singleton
    fun provideSdkBaseApiManager(): BaseApiManager {
        return BaseApiManager.getInstance()
    }

    @Provides
    @Singleton
    fun provideNetworkUtilsWrapper(@ApplicationContext context: Context): NetworkUtilsWrapper {
        return NetworkUtilsWrapper(context)
    }

}