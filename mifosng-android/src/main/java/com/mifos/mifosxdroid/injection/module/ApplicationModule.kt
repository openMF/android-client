package com.mifos.mifosxdroid.injection.module

import android.content.Context
import com.mifos.utils.NetworkUtilsWrapper
import com.mifos.utils.PrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.mifos.core.apimanager.BaseApiManager
import org.mifos.core.apimanager.BaseApiManager.Companion.getInstance
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
        val usernamePassword: Pair<String, String> = PrefManager.usernamePassword
        val baseManager = getInstance()
        baseManager.createService(
            usernamePassword.first,
            usernamePassword.second,
            PrefManager.getInstanceUrl(),
            PrefManager.getTenant(),
            false
        )
        return baseManager
    }

    @Provides
    @Singleton
    fun provideNetworkUtilsWrapper(@ApplicationContext context: Context): NetworkUtilsWrapper {
        return NetworkUtilsWrapper(context)
    }

}