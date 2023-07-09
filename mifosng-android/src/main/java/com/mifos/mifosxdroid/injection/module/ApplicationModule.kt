package com.mifos.mifosxdroid.injection.module

import android.app.Application
import android.content.Context
import com.mifos.api.BaseApiManager
import com.mifos.mifosxdroid.injection.ApplicationContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Rajan Maurya
 * Provide application-level dependencies.
 */
@Module
class ApplicationModule(protected val mApplication: Application) {
    @Provides
    fun provideApplication(): Application {
        return mApplication
    }

    @Provides
    @ApplicationContext
    fun provideContext(): Context {
        return mApplication
    }

    @Provides
    @Singleton
    fun provideBaseApiManager(): BaseApiManager {
        return BaseApiManager()
    }
}