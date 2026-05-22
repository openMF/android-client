/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.di

import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.data.activate.ActivateRepository
import com.mifos.core.data.activate.impl.ActivateRepositoryImpl
import com.mifos.core.data.searchrecord.SearchRecordRepository
import com.mifos.core.data.searchrecord.impl.SearchRecordRepositoryImpl
import com.mifos.core.data.searchrecord.local.SearchRecordLocalDataSource
import com.mifos.core.data.searchrecord.local.SearchRecordLocalDataSourceImpl
import com.mifos.core.data.repository.AmountTransferRepository
import com.mifos.core.data.repository.AppLockRepository
import com.mifos.core.data.repository.CenterDetailsRepository
import com.mifos.core.data.repository.CenterListRepository
import com.mifos.core.data.repository.ChargeRepository
import com.mifos.core.data.repository.CheckerInboxRepository
import com.mifos.core.data.repository.CheckerInboxTasksRepository
import com.mifos.core.data.repository.ClientDetailsEditRepository
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.data.repository.CreateNewCenterRepository
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.data.repository.DataTableDataRepository
import com.mifos.core.data.repository.DataTableListRepository
import com.mifos.core.data.repository.DataTableRepository
import com.mifos.core.data.repository.DataTableRowDialogRepository
import com.mifos.core.data.document.DocumentRepository
import com.mifos.core.data.document.impl.DocumentRepositoryImpl
import com.mifos.core.data.repository.DocumentCreateUpdateRepository
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.data.repository.FixedDepositRepository
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.data.repository.GroupListRepository
import com.mifos.core.data.repository.GroupLoanAccountRepository
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.core.data.repository.IndividualCollectionSheetDetailsRepository
import com.mifos.core.data.repository.LoanAccountApprovalRepository
import com.mifos.core.data.repository.LoanAccountDisbursementRepository
import com.mifos.core.data.repository.LoanAccountRepository
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.repository.LoanChargeFormRepository
import com.mifos.core.data.repository.LoanChargeRepository
import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.core.data.repository.LoanRepaymentScheduleRepository
import com.mifos.core.data.repository.LoanReschedulesRepository
import com.mifos.core.data.repository.LoanTransactionsRepository
import com.mifos.core.data.auth.LoginRepository
import com.mifos.core.data.auth.impl.LoginRepositoryImpl
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.data.note.NoteRepository
import com.mifos.core.data.note.impl.NoteRepositoryImpl
import com.mifos.core.data.note.impl.provideNoteListStore
import com.mifos.core.data.repository.OfflineDashboardRepository
import com.mifos.core.data.pathtracking.PathTrackingRepository
import com.mifos.core.data.pathtracking.impl.PathTrackingRepositoryImpl
import com.mifos.core.data.pathtracking.impl.providePathTrackingListStore
import com.mifos.core.data.repository.PinPointClientRepository
import com.mifos.core.data.repository.RecurringAccountRepository
import com.mifos.core.data.repository.ReportCategoryRepository
import com.mifos.core.data.repository.ReportDetailRepository
import com.mifos.core.data.repository.SavingsAccountActivateRepository
import com.mifos.core.data.repository.SavingsAccountApprovalRepository
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.core.data.repository.SavingsAccountTransactionReceiptRepository
import com.mifos.core.data.repository.SavingsAccountTransactionRepository
import com.mifos.core.data.search.SearchRepository
import com.mifos.core.data.search.impl.SearchRepositoryImpl
import com.mifos.core.data.syncsurvey.SyncSurveysDialogRepository
import com.mifos.core.data.syncsurvey.impl.SyncSurveysDialogRepositoryImpl
import com.mifos.core.data.repository.ShareAccountRepository
import com.mifos.core.data.repository.SignatureRepository
import com.mifos.core.data.repository.SurveyListRepository
import com.mifos.core.data.repository.SurveySubmitRepository
import com.mifos.core.data.repository.SyncCenterPayloadsRepository
import com.mifos.core.data.repository.SyncCentersDialogRepository
import com.mifos.core.data.repository.SyncClientPayloadsRepository
import com.mifos.core.data.repository.SyncClientsDialogRepository
import com.mifos.core.data.repository.SyncGroupPayloadsRepository
import com.mifos.core.data.repository.SyncGroupsDialogRepository
import com.mifos.core.data.repository.SyncLoanRepaymentTransactionRepository
import com.mifos.core.data.repository.SyncSavingsAccountTransactionRepository
import com.mifos.core.data.repository.UserVerificationRepository
import com.mifos.core.data.repositoryImp.AmountTransferRepositoryImp
import com.mifos.core.data.repositoryImp.AppLockRepositoryImpl
import com.mifos.core.data.repositoryImp.BiometricStorageAdapterImpl
import com.mifos.core.data.repositoryImp.CenterDetailsRepositoryImp
import com.mifos.core.data.repositoryImp.CenterListRepositoryImp
import com.mifos.core.data.repositoryImp.ChargeRepositoryImp
import com.mifos.core.data.repositoryImp.CheckerInboxRepositoryImp
import com.mifos.core.data.repositoryImp.CheckerInboxTasksRepositoryImp
import com.mifos.core.data.repositoryImp.ClientDetailsEditRepositoryImpl
import com.mifos.core.data.repositoryImp.ClientDetailsRepositoryImp
import com.mifos.core.data.repositoryImp.ClientIdentifiersRepositoryImp
import com.mifos.core.data.repositoryImp.ClientListRepositoryImp
import com.mifos.core.data.repositoryImp.CreateNewCenterRepositoryImp
import com.mifos.core.data.repositoryImp.CreateNewClientRepositoryImp
import com.mifos.core.data.repositoryImp.CreateNewGroupRepositoryImp
import com.mifos.core.data.repositoryImp.DataTableDataRepositoryImp
import com.mifos.core.data.repositoryImp.DataTableListRepositoryImp
import com.mifos.core.data.repositoryImp.DataTableRepositoryImp
import com.mifos.core.data.repositoryImp.DataTableRowDialogRepositoryImp
import com.mifos.core.data.repositoryImp.DocumentCreateUpdateRepositoryImp
import com.mifos.core.data.repositoryImp.DocumentListRepositoryImp
import com.mifos.core.data.repositoryImp.FixedDepositRepositoryImpl
import com.mifos.core.data.repositoryImp.GenerateCollectionSheetRepositoryImp
import com.mifos.core.data.repositoryImp.GroupDetailsRepositoryImp
import com.mifos.core.data.repositoryImp.GroupListRepositoryImp
import com.mifos.core.data.repositoryImp.GroupLoanAccountRepositoryImp
import com.mifos.core.data.repositoryImp.GroupsListRepositoryImpl
import com.mifos.core.data.repositoryImp.IndividualCollectionSheetDetailsRepositoryImp
import com.mifos.core.data.repositoryImp.LoanAccountApprovalRepositoryImp
import com.mifos.core.data.repositoryImp.LoanAccountDisbursementRepositoryImp
import com.mifos.core.data.repositoryImp.LoanAccountRepositoryImp
import com.mifos.core.data.repositoryImp.LoanAccountSummaryRepositoryImp
import com.mifos.core.data.repositoryImp.LoanChargeFormRepositoryImp
import com.mifos.core.data.repositoryImp.LoanChargeRepositoryImp
import com.mifos.core.data.repositoryImp.LoanRepaymentRepositoryImp
import com.mifos.core.data.repositoryImp.LoanRepaymentScheduleRepositoryImp
import com.mifos.core.data.repositoryImp.LoanReschedulesRepositoryImpl
import com.mifos.core.data.repositoryImp.LoanTransactionsRepositoryImp
import com.mifos.core.data.repositoryImp.NewIndividualCollectionSheetRepositoryImp
import com.mifos.core.data.repositoryImp.OfflineDashboardRepositoryImp
import com.mifos.core.data.repositoryImp.PasscodeStorageAdapterImpl
import com.mifos.core.data.repositoryImp.PinPointClientRepositoryImp
import com.mifos.core.data.repositoryImp.RecurringAccountRepositoryImp
import com.mifos.core.data.repositoryImp.ReportCategoryRepositoryImp
import com.mifos.core.data.repositoryImp.ReportDetailRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountActivateRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountApprovalRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountSummaryRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountTransactionReceiptRepositoryImpl
import com.mifos.core.data.repositoryImp.SavingsAccountTransactionRepositoryImp
import com.mifos.core.data.repositoryImp.ShareAccountRepositoryImpl
import com.mifos.core.data.repositoryImp.SignatureRepositoryImp
import com.mifos.core.data.repositoryImp.SurveyListRepositoryImp
import com.mifos.core.data.repositoryImp.SurveySubmitRepositoryImp
import com.mifos.core.data.repositoryImp.SyncCenterPayloadsRepositoryImp
import com.mifos.core.data.repositoryImp.SyncCentersDialogRepositoryImp
import com.mifos.core.data.repositoryImp.SyncClientPayloadsRepositoryImp
import com.mifos.core.data.repositoryImp.SyncClientsDialogRepositoryImp
import com.mifos.core.data.repositoryImp.SyncGroupPayloadsRepositoryImp
import com.mifos.core.data.repositoryImp.SyncGroupsDialogRepositoryImp
import com.mifos.core.data.repositoryImp.SyncLoanRepaymentTransactionRepositoryImp
import com.mifos.core.data.repositoryImp.SyncSavingsAccountTransactionRepositoryImp
import com.mifos.core.data.repositoryImp.UserVerificationRepositoryImpl
import com.mifos.core.data.infra.NetworkMonitor
import com.mifos.core.data.infra.StoreCacheManager
import com.mifos.core.data.infra.impl.NetworkMonitorImpl
import com.mifos.core.data.infra.impl.RoomFetchedAtRepository
import com.mifos.core.data.infra.impl.StoreCacheManagerImpl
import com.mifos.room.MifosDatabase
import template.core.base.store.infra.FetchedAtRepository
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.mifos.authenticator.biometrics.BiometricStorageAdapter
import org.mifos.authenticator.passcode.PasscodeStorageAdapter

val RepositoryModule = module {
    single<CoroutineDispatcher> { get(named(MifosDispatchers.IO.name)) }

    singleOf(::LoginRepositoryImpl) bind LoginRepository::class
    singleOf(::SearchRepositoryImpl) bind SearchRepository::class

    // Client
    singleOf(::ClientDetailsRepositoryImp) bind ClientDetailsRepository::class
    singleOf(::ClientListRepositoryImp) bind ClientListRepository::class
    singleOf(::ChargeRepositoryImp) bind ChargeRepository::class
    singleOf(::ClientIdentifiersRepositoryImp) bind ClientIdentifiersRepository::class
    singleOf(::CreateNewClientRepositoryImp) bind CreateNewClientRepository::class
    singleOf(::ClientDetailsEditRepositoryImpl) bind ClientDetailsEditRepository::class
    singleOf(::PinPointClientRepositoryImp) bind PinPointClientRepository::class

    // Center
    singleOf(::CenterDetailsRepositoryImp) bind CenterDetailsRepository::class
    singleOf(::CenterListRepositoryImp) bind CenterListRepository::class
    singleOf(::CreateNewCenterRepositoryImp) bind CreateNewCenterRepository::class
    singleOf(::GroupsListRepositoryImpl) bind GroupsListRepository::class

    // Group
    singleOf(::GroupDetailsRepositoryImp) bind GroupDetailsRepository::class
    singleOf(::GroupListRepositoryImp) bind GroupListRepository::class
    singleOf(::GroupLoanAccountRepositoryImp) bind GroupLoanAccountRepository::class
    singleOf(::CreateNewGroupRepositoryImp) bind CreateNewGroupRepository::class

    // Loan
    singleOf(::LoanAccountRepositoryImp) bind LoanAccountRepository::class
    singleOf(::LoanAccountApprovalRepositoryImp) bind LoanAccountApprovalRepository::class
    singleOf(::LoanAccountDisbursementRepositoryImp) bind LoanAccountDisbursementRepository::class
    singleOf(::LoanAccountSummaryRepositoryImp) bind LoanAccountSummaryRepository::class
    singleOf(::LoanChargeFormRepositoryImp) bind LoanChargeFormRepository::class
    singleOf(::LoanChargeRepositoryImp) bind LoanChargeRepository::class
    singleOf(::LoanRepaymentRepositoryImp) bind LoanRepaymentRepository::class
    singleOf(::LoanRepaymentScheduleRepositoryImp) bind LoanRepaymentScheduleRepository::class
    singleOf(::LoanTransactionsRepositoryImp) bind LoanTransactionsRepository::class
    singleOf(::LoanReschedulesRepositoryImpl) bind LoanReschedulesRepository::class

    // Account Transfer
    singleOf(::AmountTransferRepositoryImp) bind AmountTransferRepository::class

    // Savings
    singleOf(::SavingsAccountRepositoryImp) bind SavingsAccountRepository::class
    singleOf(::SavingsAccountActivateRepositoryImp) bind SavingsAccountActivateRepository::class
    singleOf(::SavingsAccountApprovalRepositoryImp) bind SavingsAccountApprovalRepository::class
    singleOf(::SavingsAccountSummaryRepositoryImp) bind SavingsAccountSummaryRepository::class
    singleOf(::SavingsAccountTransactionRepositoryImp) bind SavingsAccountTransactionRepository::class
    singleOf(::SavingsAccountTransactionReceiptRepositoryImpl) bind SavingsAccountTransactionReceiptRepository::class

    // Sync
    singleOf(::SyncCenterPayloadsRepositoryImp) bind SyncCenterPayloadsRepository::class
    singleOf(::SyncCentersDialogRepositoryImp) bind SyncCentersDialogRepository::class
    singleOf(::SyncClientPayloadsRepositoryImp) bind SyncClientPayloadsRepository::class
    singleOf(::SyncClientsDialogRepositoryImp) bind SyncClientsDialogRepository::class
    singleOf(::SyncGroupPayloadsRepositoryImp) bind SyncGroupPayloadsRepository::class
    singleOf(::SyncGroupsDialogRepositoryImp) bind SyncGroupsDialogRepository::class
    singleOf(::SyncLoanRepaymentTransactionRepositoryImp) bind SyncLoanRepaymentTransactionRepository::class
    singleOf(::SyncSavingsAccountTransactionRepositoryImp) bind SyncSavingsAccountTransactionRepository::class
    singleOf(::SyncSurveysDialogRepositoryImpl) bind SyncSurveysDialogRepository::class

    // Others
    singleOf(::ActivateRepositoryImpl) bind ActivateRepository::class
    singleOf(::CheckerInboxRepositoryImp) bind CheckerInboxRepository::class
    singleOf(::CheckerInboxTasksRepositoryImp) bind CheckerInboxTasksRepository::class
    singleOf(::DataTableDataRepositoryImp) bind DataTableDataRepository::class
    singleOf(::DataTableListRepositoryImp) bind DataTableListRepository::class
    singleOf(::DataTableRepositoryImp) bind DataTableRepository::class
    singleOf(::DataTableRowDialogRepositoryImp) bind DataTableRowDialogRepository::class
    singleOf(::DocumentCreateUpdateRepositoryImp) bind DocumentCreateUpdateRepository::class
    singleOf(::DocumentListRepositoryImp) bind DocumentListRepository::class
    singleOf(::DocumentRepositoryImpl) bind DocumentRepository::class
    singleOf(::IndividualCollectionSheetDetailsRepositoryImp) bind IndividualCollectionSheetDetailsRepository::class
    singleOf(::NewIndividualCollectionSheetRepositoryImp) bind NewIndividualCollectionSheetRepository::class
    singleOf(::GenerateCollectionSheetRepositoryImp) bind GenerateCollectionSheetRepository::class
    // Note feature — Store5 list cache + offline-first repository (Phase C Wave 7)
    single { provideNoteListStore(get(), get(), get()) }
    single<NoteRepository> {
        NoteRepositoryImpl(
            noteApi = get(),
            noteListStore = get(),
            networkMonitor = get(),
            fetchedAtRepository = get(),
        )
    }
    singleOf(::OfflineDashboardRepositoryImp) bind OfflineDashboardRepository::class
    // Path-tracking feature — Store5 list cache + offline-first repository (Phase C Wave 11)
    single { providePathTrackingListStore(get(), get(), get()) }
    single<PathTrackingRepository> {
        PathTrackingRepositoryImpl(
            pathTrackingApi = get(),
            pathTrackingListStore = get(),
            networkMonitor = get(),
            fetchedAtRepository = get(),
        )
    }
    singleOf(::ReportCategoryRepositoryImp) bind ReportCategoryRepository::class
    singleOf(::ReportDetailRepositoryImp) bind ReportDetailRepository::class
    singleOf(::SignatureRepositoryImp) bind SignatureRepository::class
    singleOf(::SurveyListRepositoryImp) bind SurveyListRepository::class
    singleOf(::SurveySubmitRepositoryImp) bind SurveySubmitRepository::class
    singleOf(::SignatureRepositoryImp) bind SignatureRepository::class
    singleOf(::SearchRecordRepositoryImpl) bind SearchRecordRepository::class
    singleOf(::SearchRecordLocalDataSourceImpl) bind SearchRecordLocalDataSource::class

    singleOf(::RecurringAccountRepositoryImp) bind RecurringAccountRepository::class
    singleOf(::ShareAccountRepositoryImpl) bind ShareAccountRepository::class
    singleOf(::FixedDepositRepositoryImpl) bind FixedDepositRepository::class

    singleOf(::AppLockRepositoryImpl) bind(AppLockRepository::class)
    singleOf(::PasscodeStorageAdapterImpl) bind(PasscodeStorageAdapter::class)
    singleOf(::BiometricStorageAdapterImpl) bind(BiometricStorageAdapter::class)
    singleOf(::UserVerificationRepositoryImpl) bind(UserVerificationRepository::class)

    includes(platformModule)

    // Framework infra (Phase B2 — `core/data/.../infra/`)
    single<NetworkMonitor> { NetworkMonitorImpl() }
    single<FetchedAtRepository> { RoomFetchedAtRepository(get<MifosDatabase>().fetchedAtDao) }
    single<StoreCacheManager> {
        StoreCacheManagerImpl(
            bookkeeperDao = get<MifosDatabase>().bookkeeperDao,
            draftDao = get<MifosDatabase>().draftDao,
        )
    }

    // Framework Room DAOs (direct injection for SubmitOutbox / Bookkeeper consumers)
    single { get<MifosDatabase>().bookkeeperDao }
    single { get<MifosDatabase>().draftDao }
    single { get<MifosDatabase>().fetchedAtDao }
}
