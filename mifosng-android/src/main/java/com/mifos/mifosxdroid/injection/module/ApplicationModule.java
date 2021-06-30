package com.mifos.mifosxdroid.injection.module;

import android.app.Application;
import android.content.Context;

import com.mifos.api.BaseApiManager;
import com.mifos.mifosxdroid.injection.ApplicationContext;

import org.mifos.core.datamanager.auth.DataManagerAuth;
import org.mifos.core.sharedpreference.UserPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Rajan Maurya
 *         Provide application-level dependencies.
 */
@Module
public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    BaseApiManager provideBaseApiManager() {
        return new BaseApiManager();
    }

    @Provides
    @Singleton
    UserPreferences provideSdkUserPreferences(Application application) {
        return new UserPreferences(application);
    }

    @Provides
    @Singleton
    org.mifos.core.apimanager.BaseApiManager provideSdkBaseApiManager(UserPreferences preferences) {
        return org.mifos.core.apimanager.BaseApiManager.Companion.getInstance(preferences);
    }

    @Provides
    @Singleton
    DataManagerAuth providesSdkDataManagerAuth(org.mifos.core.apimanager.BaseApiManager apiManager)
    {
        return DataManagerAuth.Companion.getInstance(apiManager);
    }

}
