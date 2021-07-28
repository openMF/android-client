package com.mifos.mifosxdroid.injection.module;

import android.app.Application;
import android.content.Context;

import com.mifos.api.BaseApiManager;
import com.mifos.mifosxdroid.injection.ApplicationContext;
import com.mifos.utils.PrefManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kotlin.Pair;

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
    org.mifos.core.apimanager.BaseApiManager provideSdkBaseApiManager() {
        Pair<String, String> usernamePassword = PrefManager.INSTANCE.getUsernamePassword();
        org.mifos.core.apimanager.BaseApiManager manager = org.mifos.core.apimanager.BaseApiManager
                .Companion.getInstance();
        manager.createService(usernamePassword.getFirst(),
                usernamePassword.getSecond(),
                PrefManager.INSTANCE.getInstanceUrl(),
                PrefManager.INSTANCE.getTenant(), false);
        return manager;
    }

}
