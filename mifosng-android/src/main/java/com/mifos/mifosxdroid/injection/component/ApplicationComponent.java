package com.mifos.mifosxdroid.injection.component;

import android.app.Application;
import android.content.Context;

import com.mifos.api.DataManager;
import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.api.datamanager.DataManagerCharge;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.api.datamanager.DataManagerOffices;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.api.datamanager.DataManagerStaff;
import com.mifos.api.local.databasehelper.DatabaseHelperCenter;
import com.mifos.api.local.databasehelper.DatabaseHelperCharge;
import com.mifos.api.local.databasehelper.DatabaseHelperClient;
import com.mifos.api.local.databasehelper.DatabaseHelperDataTable;
import com.mifos.api.local.databasehelper.DatabaseHelperGroups;
import com.mifos.api.local.databasehelper.DatabaseHelperLoan;
import com.mifos.api.local.databasehelper.DatabaseHelperOffices;
import com.mifos.api.local.databasehelper.DatabaseHelperSavings;
import com.mifos.api.local.databasehelper.DatabaseHelperStaff;
import com.mifos.mifosxdroid.injection.ApplicationContext;
import com.mifos.mifosxdroid.injection.module.ApplicationModule;

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
    DataManagerOffices dataManagerOffices();
    DataManagerStaff dataManagerStaff();
    DataManagerLoan dataManagerLoan();
    DataManagerSavings dataManagerSavings();


    DatabaseHelperClient databaseHelperClient();
    DatabaseHelperCenter databaseHelperCenter();
    DatabaseHelperGroups databaseHelperGroup();
    DatabaseHelperDataTable databaseHelperDataTable();
    DatabaseHelperCharge databaseHelperCharge();
    DatabaseHelperOffices databaseHelperOffices();
    DatabaseHelperStaff databaseHelperStaff();
    DatabaseHelperLoan databaseHelperLoan();
    DatabaseHelperSavings databaseHelperSavings();

}
