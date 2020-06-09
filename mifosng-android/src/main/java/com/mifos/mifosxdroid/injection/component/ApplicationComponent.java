package com.mifos.mifosxdroid.injection.component;

import android.app.Application;
import android.content.Context;

import com.mifos.api.DataManager;
import com.mifos.api.datamanager.DataManagerAuth;
import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.api.datamanager.DataManagerCharge;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerCollectionSheet;
import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.api.datamanager.DataManagerDocument;
import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.api.datamanager.DataManagerNote;
import com.mifos.api.datamanager.DataManagerOffices;
import com.mifos.api.datamanager.DataManagerRunReport;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.api.datamanager.DataManagerSearch;
import com.mifos.api.datamanager.DataManagerStaff;
import com.mifos.api.datamanager.DataManagerSurveys;
import com.mifos.api.local.databasehelper.DatabaseHelperCenter;
import com.mifos.api.local.databasehelper.DatabaseHelperCharge;
import com.mifos.api.local.databasehelper.DatabaseHelperClient;
import com.mifos.api.local.databasehelper.DatabaseHelperDataTable;
import com.mifos.api.local.databasehelper.DatabaseHelperGroups;
import com.mifos.api.local.databasehelper.DatabaseHelperLoan;
import com.mifos.api.local.databasehelper.DatabaseHelperNote;
import com.mifos.api.local.databasehelper.DatabaseHelperOffices;
import com.mifos.api.local.databasehelper.DatabaseHelperSavings;
import com.mifos.api.local.databasehelper.DatabaseHelperStaff;
import com.mifos.api.local.databasehelper.DatabaseHelperSurveys;
import com.mifos.mifosxdroid.activity.pathtracking.PathTrackingService;
import com.mifos.mifosxdroid.injection.ApplicationContext;
import com.mifos.mifosxdroid.injection.module.ApplicationModule;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncCenter;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncClient;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncGroup;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncLoanRepayment;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncSavingsAccount;

import javax.inject.Singleton;

import dagger.Component;


/**
 * @author Rajan Maurya
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(PathTrackingService pathTrackingService);

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
    DataManagerSurveys dataManagerSurveys();
    DataManagerDocument dataManagerDocument();
    DataManagerSearch dataManagerSearch();
    DataManagerRunReport dataManagerRunReport();
    DataManagerAuth dataManagerAuth();
    DataManagerNote dataManagerNote();
    DataManagerCollectionSheet dataManagerCollectionSheet();


    DatabaseHelperClient databaseHelperClient();
    DatabaseHelperCenter databaseHelperCenter();
    DatabaseHelperGroups databaseHelperGroup();
    DatabaseHelperDataTable databaseHelperDataTable();
    DatabaseHelperCharge databaseHelperCharge();
    DatabaseHelperOffices databaseHelperOffices();
    DatabaseHelperStaff databaseHelperStaff();
    DatabaseHelperLoan databaseHelperLoan();
    DatabaseHelperSavings databaseHelperSavings();
    DatabaseHelperSurveys databaseHelperSurveys();
    DatabaseHelperNote databaseHelperNote();

    void inject(OfflineSyncCenter offlineSyncCenter);

    void inject(OfflineSyncClient offlineSyncClient);

    void inject(OfflineSyncGroup offlineSyncGroup);

    void inject(OfflineSyncLoanRepayment offlineSyncLoanRepayment);

    void inject(OfflineSyncSavingsAccount offlineSyncSavingsAccount);
}
