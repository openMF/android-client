package com.mifos.mifosxdroid.injection.component

import android.app.Application
import android.content.Context
import com.mifos.api.DataManager
import com.mifos.api.datamanager.DataManagerAuth
import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.api.datamanager.DataManagerCharge
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerCollectionSheet
import com.mifos.api.datamanager.DataManagerDataTable
import com.mifos.api.datamanager.DataManagerDocument
import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.api.datamanager.DataManagerNote
import com.mifos.api.datamanager.DataManagerOffices
import com.mifos.api.datamanager.DataManagerRunReport
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.api.datamanager.DataManagerSearch
import com.mifos.api.datamanager.DataManagerStaff
import com.mifos.api.datamanager.DataManagerSurveys
import com.mifos.api.local.databasehelper.DatabaseHelperCenter
import com.mifos.api.local.databasehelper.DatabaseHelperCharge
import com.mifos.api.local.databasehelper.DatabaseHelperClient
import com.mifos.api.local.databasehelper.DatabaseHelperDataTable
import com.mifos.api.local.databasehelper.DatabaseHelperGroups
import com.mifos.api.local.databasehelper.DatabaseHelperLoan
import com.mifos.api.local.databasehelper.DatabaseHelperNote
import com.mifos.api.local.databasehelper.DatabaseHelperOffices
import com.mifos.api.local.databasehelper.DatabaseHelperSavings
import com.mifos.api.local.databasehelper.DatabaseHelperStaff
import com.mifos.api.local.databasehelper.DatabaseHelperSurveys
import com.mifos.mifosxdroid.activity.pathtracking.PathTrackingService
import com.mifos.mifosxdroid.injection.ApplicationContext
import com.mifos.mifosxdroid.injection.module.ApplicationModule
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncCenter
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncClient
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncGroup
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncLoanRepayment
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncSavingsAccount
import dagger.Component
import javax.inject.Singleton

/**
 * @author Rajan Maurya
 */
@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(pathTrackingService: PathTrackingService)

    @ApplicationContext
    fun context(): Context
    fun application(): Application
    fun dataManager(): DataManager
    fun dataManagerClient(): DataManagerClient
    fun dataManagerGroups(): DataManagerGroups
    fun dataManagerCenters(): DataManagerCenter
    fun dataManagerDataTable(): DataManagerDataTable
    fun dataManagerCharge(): DataManagerCharge
    fun dataManagerOffices(): DataManagerOffices
    fun dataManagerStaff(): DataManagerStaff
    fun dataManagerLoan(): DataManagerLoan
    fun dataManagerSavings(): DataManagerSavings
    fun dataManagerSurveys(): DataManagerSurveys
    fun dataManagerDocument(): DataManagerDocument
    fun dataManagerSearch(): DataManagerSearch
    fun dataManagerRunReport(): DataManagerRunReport
    fun dataManagerAuth(): DataManagerAuth
    fun dataManagerNote(): DataManagerNote
    fun dataManagerCollectionSheet(): DataManagerCollectionSheet
    fun databaseHelperClient(): DatabaseHelperClient
    fun databaseHelperCenter(): DatabaseHelperCenter
    fun databaseHelperGroup(): DatabaseHelperGroups
    fun databaseHelperDataTable(): DatabaseHelperDataTable
    fun databaseHelperCharge(): DatabaseHelperCharge
    fun databaseHelperOffices(): DatabaseHelperOffices
    fun databaseHelperStaff(): DatabaseHelperStaff
    fun databaseHelperLoan(): DatabaseHelperLoan
    fun databaseHelperSavings(): DatabaseHelperSavings
    fun databaseHelperSurveys(): DatabaseHelperSurveys
    fun databaseHelperNote(): DatabaseHelperNote
    fun inject(offlineSyncCenter: OfflineSyncCenter)
    fun inject(offlineSyncClient: OfflineSyncClient)
    fun inject(offlineSyncGroup: OfflineSyncGroup)
    fun inject(offlineSyncLoanRepayment: OfflineSyncLoanRepayment)
    fun inject(offlineSyncSavingsAccount: OfflineSyncSavingsAccount)
}