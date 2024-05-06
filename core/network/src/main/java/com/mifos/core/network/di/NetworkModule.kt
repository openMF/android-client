package com.mifos.core.network.di

import android.content.Context
import androidx.core.os.trace
import coil.ImageLoader
import coil.util.DebugLogger
import com.mifos.core.datastore.PrefManager
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