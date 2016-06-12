package com.mifos.mifosxdroid.injection.component;

import android.app.Application;
import android.content.Context;

import com.mifos.api.BaseApiManager;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.injection.ApplicationContext;
import com.mifos.mifosxdroid.injection.module.ApplicationModule;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Component;


/**
 * @author Rajan Maurya
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {


    @ApplicationContext
    Context context();

    Application application();

    BaseApiManager baseApiManager();

    DataManager dataManager();

    Bus eventBus();

}
