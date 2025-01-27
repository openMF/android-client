/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.di

import android.content.Context
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.data.repository.LoginRepository
import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.data.repository.OfflineDashboardRepository
import com.mifos.core.data.repository.PreferenceRepository
import com.mifos.core.data.repository.SavingsAccountActivateRepository
import com.mifos.core.data.repository.SavingsAccountApprovalRepository
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.core.data.repository.SavingsAccountTransactionRepository
import com.mifos.core.data.repository.SyncCenterPayloadsRepository
import com.mifos.core.data.repository.SyncCentersDialogRepository
import com.mifos.core.data.repository.SyncClientPayloadsRepository
import com.mifos.core.data.repository.SyncClientsDialogRepository
import com.mifos.core.data.repository.SyncGroupPayloadsRepository
import com.mifos.core.data.repository.SyncGroupsDialogRepository
import com.mifos.core.data.repository.SyncLoanRepaymentTransactionRepository
import com.mifos.core.data.repository.SyncSavingsAccountTransactionRepository
import com.mifos.core.data.repository.SyncSurveysDialogRepository
import com.mifos.core.data.repositoryImp.ClientDetailsRepositoryImp
import com.mifos.core.data.repositoryImp.ClientListRepositoryImp
import com.mifos.core.data.repositoryImp.CreateNewClientRepositoryImp
import com.mifos.core.data.repositoryImp.DocumentDialogRepositoryImp
import com.mifos.core.data.repositoryImp.LoginRepositoryImp
import com.mifos.core.data.repositoryImp.NoteRepositoryImp
import com.mifos.core.data.repositoryImp.OfflineDashboardRepositoryImp
import com.mifos.core.data.repositoryImp.PreferenceRepositoryImpl
import com.mifos.core.data.repositoryImp.SavingsAccountActivateRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountApprovalRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountSummaryRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountTransactionRepositoryImp
import com.mifos.core.data.repositoryImp.SyncCenterPayloadsRepositoryImp
import com.mifos.core.data.repositoryImp.SyncCentersDialogRepositoryImp
import com.mifos.core.data.repositoryImp.SyncClientPayloadsRepositoryImp
import com.mifos.core.data.repositoryImp.SyncClientsDialogRepositoryImp
import com.mifos.core.data.repositoryImp.SyncGroupPayloadsRepositoryImp
import com.mifos.core.data.repositoryImp.SyncGroupsDialogRepositoryImp
import com.mifos.core.data.repositoryImp.SyncLoanRepaymentTransactionRepositoryImp
import com.mifos.core.data.repositoryImp.SyncSavingsAccountTransactionRepositoryImp
import com.mifos.core.data.repositoryImp.SyncSurveysDialogRepositoryImp
import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerDocument
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerNote
import com.mifos.core.network.datamanager.DataManagerOffices
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.core.network.datamanager.DataManagerStaff
import com.mifos.core.network.datamanager.DataManagerSurveys
import com.mifos.core.network.datamanger.DataManagerAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("TooManyFunctions")
object RepositoryModule {

    @Provides
    fun providesClientDetailsRepository(dataManagerClient: DataManagerClient): ClientDetailsRepository =
        ClientDetailsRepositoryImp(dataManagerClient)

    @Singleton
    @Provides
    fun provideClientListRepository(dataManagerClient: DataManagerClient): ClientListRepository =
        ClientListRepositoryImp(dataManagerClient)

    @Provides
    fun providesLoginRepository(dataManagerAuth: DataManagerAuth): LoginRepository =
        LoginRepositoryImp(dataManagerAuth)

    @Provides
    fun providesSavingsAccountSummaryRepository(dataManagerSavings: DataManagerSavings): SavingsAccountSummaryRepository {
        return SavingsAccountSummaryRepositoryImp(dataManagerSavings)
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
    fun providesCreateNewClientRepository(
        dataManagerClient: DataManagerClient,
        dataManagerOffices: DataManagerOffices,
        dataManagerStaff: DataManagerStaff,
    ): CreateNewClientRepository {
        return CreateNewClientRepositoryImp(dataManagerClient, dataManagerOffices, dataManagerStaff)
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
        dataManagerClient: DataManagerClient,
    ): SyncGroupsDialogRepository {
        return SyncGroupsDialogRepositoryImp(
            dataManagerGroups,
            dataManagerLoan,
            dataManagerSavings,
            dataManagerClient,
        )
    }

    @Provides
    fun providesSyncClientsDialogRepository(
        dataManagerClient: DataManagerClient,
        dataManagerLoan: DataManagerLoan,
        dataManagerSavings: DataManagerSavings,
    ): SyncClientsDialogRepository {
        return SyncClientsDialogRepositoryImp(
            dataManagerClient,
            dataManagerLoan,
            dataManagerSavings,
        )
    }

    @Provides
    fun providesSyncCentersDialogRepository(
        dataManagerCenter: DataManagerCenter,
        dataManagerLoan: DataManagerLoan,
        dataManagerSavings: DataManagerSavings,
        dataManagerGroups: DataManagerGroups,
        dataManagerClient: DataManagerClient,
    ): SyncCentersDialogRepository {
        return SyncCentersDialogRepositoryImp(
            dataManagerCenter,
            dataManagerLoan,
            dataManagerSavings,
            dataManagerGroups,
            dataManagerClient,
        )
    }

    @Provides
    fun providesOfflineDashboardRepository(
        dataManagerClient: DataManagerClient,
        dataManagerGroups: DataManagerGroups,
        dataManagerCenter: DataManagerCenter,
        dataManagerLoan: DataManagerLoan,
        dataManagerSavings: DataManagerSavings,
    ): OfflineDashboardRepository {
        return OfflineDashboardRepositoryImp(
            dataManagerClient,
            dataManagerGroups,
            dataManagerCenter,
            dataManagerLoan,
            dataManagerSavings,
        )
    }

    @Provides
    fun providesSyncCenterPayloadsRepository(dataManagerCenter: DataManagerCenter): SyncCenterPayloadsRepository {
        return SyncCenterPayloadsRepositoryImp(dataManagerCenter)
    }

    @Provides
    fun providesSyncSavingsAccountTransactionRepository(
        dataManagerSavings: DataManagerSavings,
        dataManagerLoan: DataManagerLoan,
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

    @Provides
    @Singleton
    fun providePrefManager(@ApplicationContext context: Context): PrefManager {
        return PrefManager(context)
    }

    @Provides
    fun providesPreferenceRepository(prefManager: PrefManager): PreferenceRepository {
        return PreferenceRepositoryImpl(prefManager)
    }
}
