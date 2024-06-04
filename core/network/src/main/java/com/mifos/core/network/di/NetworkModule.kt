package com.mifos.core.network.di

import android.content.Context
import android.util.Log
import androidx.core.os.trace
import coil.ImageLoader
import coil.util.DebugLogger
import com.mifos.core.model.getInstanceUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import okhttp3.OkHttpClient
import org.mifos.core.apimanager.BaseApiManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseApiManager(prefManager: com.mifos.core.datastore.PrefManager): com.mifos.core.network.BaseApiManager {
        return com.mifos.core.network.BaseApiManager(prefManager)
    }

    @Provides
    @Singleton
    fun provideSdkBaseApiManager(prefManager: com.mifos.core.datastore.PrefManager): BaseApiManager {
        val usernamePassword: Pair<String, String> = prefManager.usernamePassword
        val baseManager = BaseApiManager.getInstance()
        baseManager.createService(
            usernamePassword.first,
            usernamePassword.second,
            prefManager.getServerConfig.getInstanceUrl(),
            prefManager.getServerConfig.tenant,
            false
        )
        return baseManager
    }

    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory = trace("MifosHttpClient") {
        OkHttpClient.Builder().build()
    }


    @Provides
    @Singleton
    fun provideImageLoader(
        okHttpCallFactory: dagger.Lazy<Call.Factory>,
        @ApplicationContext context: Context
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .callFactory { okHttpCallFactory.get() }
            .apply {
                logger(DebugLogger())
            }.build()
    }
}