package com.mifos.mifosxdroid.injection.module

import android.content.Context
import com.mifos.utils.NetworkUtilsWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * @author Rajan Maurya
 * Provide application-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

//    @Provides
//    @Singleton
//    fun provideBaseApiManager(prefManager: PrefManager): com.mifos.core.network.BaseApiManager {
//        return com.mifos.core.network.BaseApiManager(prefManager)
//    }
//
//    @Provides
//    @Singleton
//    fun provideSdkBaseApiManager(prefManager: PrefManager): BaseApiManager {
//        val usernamePassword: Pair<String, String> = prefManager.usernamePassword
//        val baseManager = getInstance()
//        baseManager.createService(
//            usernamePassword.first,
//            usernamePassword.second,
//            prefManager.getInstanceUrl(),
//            prefManager.getTenant(),
//            false
//        )
//        return baseManager
//    }

    @Provides
    @Singleton
    fun provideNetworkUtilsWrapper(@ApplicationContext context: Context): NetworkUtilsWrapper {
        return NetworkUtilsWrapper(context)
    }

}