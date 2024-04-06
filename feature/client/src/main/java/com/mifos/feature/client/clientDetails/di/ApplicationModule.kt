package com.mifos.feature.client.clientDetails.di

import android.content.Context
import androidx.compose.ui.util.trace
import coil.ImageLoader
import coil.util.DebugLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

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