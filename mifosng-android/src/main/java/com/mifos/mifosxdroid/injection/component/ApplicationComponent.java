package com.mifos.mifosxdroid.injection.component;

import android.app.Application;
import android.content.Context;

import com.mifos.api.DataManager;
import com.mifos.api.DataManagerOffline;
import com.mifos.api.DataManagerOnline;
import com.mifos.api.local.DatabaseHelper;
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
    DatabaseHelper databaseHelper();
    DataManager dataManager();
    DataManagerOnline dataManagerOnline();
    DataManagerOffline dataManagerOffline();
    Bus eventBus();

}
