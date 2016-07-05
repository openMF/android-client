package com.mifos.mifosxdroid.injection.component;

import android.app.Application;
import android.content.Context;

import com.mifos.api.DataManager;
import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.api.datamanager.DataManagerCharge;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.api.local.databasehelper.DatabaseHelperCenter;
import com.mifos.api.local.databasehelper.DatabaseHelperCharge;
import com.mifos.api.local.databasehelper.DatabaseHelperClient;
import com.mifos.api.local.databasehelper.DatabaseHelperDataTable;
import com.mifos.api.local.databasehelper.DatabaseHelperGroups;
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
    DataManager dataManager();

    DataManagerClient dataManagerClient();
    DataManagerGroups dataManagerGroups();
    DataManagerCenter dataManagerCenters();
    DataManagerDataTable dataManagerDataTable();
    DataManagerCharge dataManagerCharge();


    DatabaseHelperClient databaseHelperClient();
    DatabaseHelperCenter databaseHelperCenter();
    DatabaseHelperGroups databaseHelperGroup();
    DatabaseHelperDataTable databaseHelperDataTable();
    DatabaseHelperCharge databaseHelperCharge();

    Bus eventBus();

}
