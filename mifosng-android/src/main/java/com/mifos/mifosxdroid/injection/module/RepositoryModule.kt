package com.mifos.mifosxdroid.injection.module

import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.data.repository.SavingsAccountActivateRepository
import com.mifos.core.data.repository.SavingsAccountApprovalRepository
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.core.data.repository.SavingsAccountTransactionRepository
import com.mifos.core.data.repository.SyncGroupPayloadsRepository
import com.mifos.core.data.repository_imp.DocumentDialogRepositoryImp
import com.mifos.core.data.repository_imp.NoteRepositoryImp
import com.mifos.core.data.repository_imp.SyncGroupPayloadsRepositoryImp
import com.mifos.core.network.DataManager
import com.mifos.core.network.datamanager.DataManagerAuth
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerDataTable
import com.mifos.core.network.datamanager.DataManagerDocument
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerNote
import com.mifos.core.network.datamanager.DataManagerOffices
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.core.network.datamanager.DataManagerStaff
import com.mifos.core.network.datamanager.DataManagerSurveys
import com.mifos.feature.settings.syncSurvey.SyncSurveysDialogRepository
import com.mifos.feature.settings.syncSurvey.SyncSurveysDialogRepositoryImp
import com.mifos.mifosxdroid.activity.login.LoginRepository
import com.mifos.mifosxdroid.activity.login.LoginRepositoryImp
import com.mifos.core.data.repository.SyncCentersDialogRepository
import com.mifos.core.data.repository_imp.SyncCentersDialogRepositoryImp
import com.mifos.core.data.repository.SyncClientsDialogRepository
import com.mifos.core.data.repository_imp.SyncClientsDialogRepositoryImp
import com.mifos.core.data.repository.SyncGroupsDialogRepository
import com.mifos.core.data.repository_imp.SyncGroupsDialogRepositoryImp
import com.mifos.mifosxdroid.offline.offlinedashbarod.OfflineDashboardRepository
import com.mifos.mifosxdroid.offline.offlinedashbarod.OfflineDashboardRepositoryImp
import com.mifos.core.data.repository.SyncCenterPayloadsRepository
import com.mifos.core.data.repository.SyncSavingsAccountTransactionRepository
import com.mifos.core.data.repository_imp.SavingsAccountActivateRepositoryImp
import com.mifos.core.data.repository_imp.SavingsAccountApprovalRepositoryImp
import com.mifos.core.data.repository_imp.SavingsAccountRepositoryImp
import com.mifos.core.data.repository_imp.SavingsAccountSummaryRepositoryImp
import com.mifos.core.data.repository_imp.SavingsAccountTransactionRepositoryImp
import com.mifos.core.data.repository_imp.SyncCenterPayloadsRepositoryImp
import com.mifos.core.data.repository_imp.SyncSavingsAccountTransactionRepositoryImp
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadsRepository
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadsRepositoryImp
import com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition.SyncLoanRepaymentTransactionRepository
import com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition.SyncLoanRepaymentTransactionRepositoryImp
import com.mifos.mifosxdroid.online.activate.ActivateRepository
import com.mifos.mifosxdroid.online.activate.ActivateRepositoryImp
import com.mifos.mifosxdroid.online.centerlist.CenterListRepository
import com.mifos.mifosxdroid.online.centerlist.CenterListRepositoryImp
import com.mifos.mifosxdroid.online.collectionsheet.CollectionSheetRepository
import com.mifos.mifosxdroid.online.collectionsheet.CollectionSheetRepositoryImp
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.data.repository_imp.CreateNewClientRepositoryImp
import com.mifos.mifosxdroid.online.datatable.DataTableRepository
import com.mifos.mifosxdroid.online.datatable.DataTableRepositoryImp
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListRepository
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListRepositoryImp
import com.mifos.mifosxdroid.online.groupdetails.GroupDetailsRepository
import com.mifos.mifosxdroid.online.groupdetails.GroupDetailsRepositoryImp
import com.mifos.mifosxdroid.online.groupslist.GroupsListRepository
import com.mifos.mifosxdroid.online.groupslist.GroupsListRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Aditya Gupta on 06/08/23.
 */

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun providesLoginRepository(dataManagerAuth: DataManagerAuth): LoginRepository {
        return LoginRepositoryImp(dataManagerAuth)
    }

    @Provides
    fun providesCenterListRepository(dataManagerCenter: DataManagerCenter): CenterListRepository {
        return CenterListRepositoryImp(dataManagerCenter)
    }

    @Provides
    fun providesGroupDetailsRepository(dataManagerGroups: DataManagerGroups): GroupDetailsRepository {
        return GroupDetailsRepositoryImp(dataManagerGroups)
    }

    @Provides
    fun providesActivateRepository(
        dataManagerClient: DataManagerClient,
        dataManagerCenter: DataManagerCenter,
        dataManagerGroups: DataManagerGroups
    ): ActivateRepository {
        return ActivateRepositoryImp(dataManagerClient, dataManagerCenter, dataManagerGroups)
    }

    @Provides
    fun providesGroupsListRepository(dataManagerGroups: DataManagerGroups): GroupsListRepository {
        return GroupsListRepositoryImp(dataManagerGroups)
    }

    @Provides
    fun providesSavingsAccountSummaryRepository(dataManagerSavings: DataManagerSavings): SavingsAccountSummaryRepository {
        return SavingsAccountSummaryRepositoryImp(dataManagerSavings)
    }

    @Provides
    fun providesDataTableRepository(dataManagerDataTable: DataManagerDataTable): DataTableRepository {
        return DataTableRepositoryImp(dataManagerDataTable)
    }


    @Provides
    fun providesNoteRepository(dataManagerNote: DataManagerNote): NoteRepository {
        return NoteRepositoryImp(dataManagerNote)
    }

    @Provides
    fun providesSavingAccountRepository(dataManagerSavings: DataManagerSavings): SavingsAccountRepository {
        return SavingsAccountRepositoryImp(dataManagerSavings)
    }

    @Provides
    fun providesCollectionSheetRepository(dataManager: DataManager): CollectionSheetRepository {
        return CollectionSheetRepositoryImp(dataManager)
    }

    @Provides
    fun providesCreateNewClientRepository(
        dataManagerClient: DataManagerClient,
        dataManagerOffices: DataManagerOffices,
        dataManagerStaff: DataManagerStaff
    ): CreateNewClientRepository {
        return CreateNewClientRepositoryImp(dataManagerClient, dataManagerOffices, dataManagerStaff)
    }


    @Provides
    fun providesDataTableListRepository(
        dataManagerLoan: DataManagerLoan,
        dataManager: DataManager,
        dataManagerClient: DataManagerClient
    ): DataTableListRepository {
        return DataTableListRepositoryImp(dataManagerLoan, dataManager, dataManagerClient)
    }

    @Provides
    fun providesSavingsAccountTransactionRepository(dataManagerSavings: DataManagerSavings): SavingsAccountTransactionRepository {
        return SavingsAccountTransactionRepositoryImp(dataManagerSavings)
    }

    @Provides
    fun providesSavingsAccountActivateRepository(dataManagerSavings: DataManagerSavings): SavingsAccountActivateRepository {
        return SavingsAccountActivateRepositoryImp(dataManagerSavings)
    }

    @Provides
    fun providesSavingsAccountApprovalRepository(dataManagerSavings: DataManagerSavings): SavingsAccountApprovalRepository {
        return SavingsAccountApprovalRepositoryImp(dataManagerSavings)
    }

    @Provides
    fun providesDocumentDialogRepository(dataManagerDocument: DataManagerDocument): DocumentDialogRepository {
        return DocumentDialogRepositoryImp(dataManagerDocument)
    }


    @Provides
    fun providesSyncSurveysDialogRepository(dataManagerSurvey: DataManagerSurveys): SyncSurveysDialogRepository {
        return SyncSurveysDialogRepositoryImp(dataManagerSurvey)
    }

    @Provides
    fun providesSyncGroupsDialogRepository(
        dataManagerGroups: DataManagerGroups,
        dataManagerLoan: DataManagerLoan,
        dataManagerSavings: DataManagerSavings,
        dataManagerClient: DataManagerClient
    ): SyncGroupsDialogRepository {
        return SyncGroupsDialogRepositoryImp(
            dataManagerGroups,
            dataManagerLoan,
            dataManagerSavings,
            dataManagerClient
        )
    }

    @Provides
    fun providesSyncClientsDialogRepository(
        dataManagerClient: DataManagerClient,
        dataManagerLoan: DataManagerLoan,
        dataManagerSavings: DataManagerSavings
    ): SyncClientsDialogRepository {
        return SyncClientsDialogRepositoryImp(
            dataManagerClient,
            dataManagerLoan,
            dataManagerSavings
        )
    }

    @Provides
    fun providesSyncCentersDialogRepository(
        dataManagerCenter: DataManagerCenter,
        dataManagerLoan: DataManagerLoan,
        dataManagerSavings: DataManagerSavings,
        dataManagerGroups: DataManagerGroups,
        dataManagerClient: DataManagerClient
    ): SyncCentersDialogRepository {
        return SyncCentersDialogRepositoryImp(
            dataManagerCenter,
            dataManagerLoan,
            dataManagerSavings,
            dataManagerGroups,
            dataManagerClient
        )
    }

    @Provides
    fun providesOfflineDashboardRepository(
        dataManagerClient: DataManagerClient,
        dataManagerGroups: DataManagerGroups,
        dataManagerCenter: DataManagerCenter,
        dataManagerLoan: DataManagerLoan,
        dataManagerSavings: DataManagerSavings
    ): OfflineDashboardRepository {
        return OfflineDashboardRepositoryImp(
            dataManagerClient,
            dataManagerGroups,
            dataManagerCenter,
            dataManagerLoan,
            dataManagerSavings
        )
    }

    @Provides
    fun providesSyncCenterPayloadsRepository(dataManagerCenter: DataManagerCenter): SyncCenterPayloadsRepository {
        return SyncCenterPayloadsRepositoryImp(dataManagerCenter)
    }

    @Provides
    fun providesSyncSavingsAccountTransactionRepository(
        dataManagerSavings: DataManagerSavings,
        dataManagerLoan: DataManagerLoan
    ): SyncSavingsAccountTransactionRepository {
        return SyncSavingsAccountTransactionRepositoryImp(dataManagerSavings, dataManagerLoan)
    }

    @Provides
    fun providesSyncLoanRepaymentTransactionRepository(dataManagerLoan: DataManagerLoan): SyncLoanRepaymentTransactionRepository {
        return SyncLoanRepaymentTransactionRepositoryImp(dataManagerLoan)
    }

    @Provides
    fun providesSyncGroupPayloadsRepository(dataManagerGroups: DataManagerGroups): SyncGroupPayloadsRepository {
        return SyncGroupPayloadsRepositoryImp(dataManagerGroups)
    }

    @Provides
    fun providesSyncClientPayloadsRepository(dataManagerClient: DataManagerClient): SyncClientPayloadsRepository {
        return SyncClientPayloadsRepositoryImp(dataManagerClient)
    }
}