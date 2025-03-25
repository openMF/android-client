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

import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.data.repository.ActivateRepository
import com.mifos.core.data.repository.CenterDetailsRepository
import com.mifos.core.data.repository.CenterListRepository
import com.mifos.core.data.repository.ChargeDialogRepository
import com.mifos.core.data.repository.CheckerInboxRepository
import com.mifos.core.data.repository.CheckerInboxTasksRepository
import com.mifos.core.data.repository.ClientChargeRepository
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.repository.ClientIdentifierDialogRepository
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.data.repository.CreateNewCenterRepository
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.data.repository.DataTableDataRepository
import com.mifos.core.data.repository.DataTableListRepository
import com.mifos.core.data.repository.DataTableRepository
import com.mifos.core.data.repository.DataTableRowDialogRepository
import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.data.repository.GroupListRepository
import com.mifos.core.data.repository.GroupLoanAccountRepository
import com.mifos.core.data.repository.IndividualCollectionSheetDetailsRepository
import com.mifos.core.data.repository.LoanAccountApprovalRepository
import com.mifos.core.data.repository.LoanAccountDisbursementRepository
import com.mifos.core.data.repository.LoanAccountRepository
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.repository.LoanChargeDialogRepository
import com.mifos.core.data.repository.LoanChargeRepository
import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.core.data.repository.LoanRepaymentScheduleRepository
import com.mifos.core.data.repository.LoanTransactionsRepository
import com.mifos.core.data.repository.LoginRepository
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.data.repository.OfflineDashboardRepository
import com.mifos.core.data.repository.PathTrackingRepository
import com.mifos.core.data.repository.PinPointClientRepository
import com.mifos.core.data.repository.ReportCategoryRepository
import com.mifos.core.data.repository.ReportDetailRepository
import com.mifos.core.data.repository.SavingsAccountActivateRepository
import com.mifos.core.data.repository.SavingsAccountApprovalRepository
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.core.data.repository.SavingsAccountTransactionRepository
import com.mifos.core.data.repository.SearchRepository
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
import com.mifos.core.data.repositoryImp.ActivateRepositoryImp
import com.mifos.core.data.repositoryImp.CenterDetailsRepositoryImp
import com.mifos.core.data.repositoryImp.CenterListRepositoryImp
import com.mifos.core.data.repositoryImp.ChargeDialogRepositoryImp
import com.mifos.core.data.repositoryImp.CheckerInboxRepositoryImp
import com.mifos.core.data.repositoryImp.CheckerInboxTasksRepositoryImp
import com.mifos.core.data.repositoryImp.ClientChargeRepositoryImp
import com.mifos.core.data.repositoryImp.ClientDetailsRepositoryImp
import com.mifos.core.data.repositoryImp.ClientIdentifierDialogRepositoryImp
import com.mifos.core.data.repositoryImp.ClientIdentifiersRepositoryImp
import com.mifos.core.data.repositoryImp.ClientListRepositoryImp
import com.mifos.core.data.repositoryImp.CreateNewCenterRepositoryImp
import com.mifos.core.data.repositoryImp.CreateNewClientRepositoryImp
import com.mifos.core.data.repositoryImp.CreateNewGroupRepositoryImp
import com.mifos.core.data.repositoryImp.DataTableDataRepositoryImp
import com.mifos.core.data.repositoryImp.DataTableListRepositoryImp
import com.mifos.core.data.repositoryImp.DataTableRepositoryImp
import com.mifos.core.data.repositoryImp.DataTableRowDialogRepositoryImp
import com.mifos.core.data.repositoryImp.DocumentDialogRepositoryImp
import com.mifos.core.data.repositoryImp.DocumentListRepositoryImp
import com.mifos.core.data.repositoryImp.GenerateCollectionSheetRepositoryImp
import com.mifos.core.data.repositoryImp.GroupDetailsRepositoryImp
import com.mifos.core.data.repositoryImp.GroupListRepositoryImp
import com.mifos.core.data.repositoryImp.GroupLoanAccountRepositoryImp
import com.mifos.core.data.repositoryImp.IndividualCollectionSheetDetailsRepositoryImp
import com.mifos.core.data.repositoryImp.LoanAccountApprovalRepositoryImp
import com.mifos.core.data.repositoryImp.LoanAccountDisbursementRepositoryImp
import com.mifos.core.data.repositoryImp.LoanAccountRepositoryImp
import com.mifos.core.data.repositoryImp.LoanAccountSummaryRepositoryImp
import com.mifos.core.data.repositoryImp.LoanChargeDialogRepositoryImp
import com.mifos.core.data.repositoryImp.LoanChargeRepositoryImp
import com.mifos.core.data.repositoryImp.LoanRepaymentRepositoryImp
import com.mifos.core.data.repositoryImp.LoanRepaymentScheduleRepositoryImp
import com.mifos.core.data.repositoryImp.LoanTransactionsRepositoryImp
import com.mifos.core.data.repositoryImp.LoginRepositoryImp
import com.mifos.core.data.repositoryImp.NewIndividualCollectionSheetRepositoryImp
import com.mifos.core.data.repositoryImp.NoteRepositoryImp
import com.mifos.core.data.repositoryImp.OfflineDashboardRepositoryImp
import com.mifos.core.data.repositoryImp.PathTrackingRepositoryImp
import com.mifos.core.data.repositoryImp.PinPointClientRepositoryImp
import com.mifos.core.data.repositoryImp.ReportCategoryRepositoryImp
import com.mifos.core.data.repositoryImp.ReportDetailRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountActivateRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountApprovalRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountSummaryRepositoryImp
import com.mifos.core.data.repositoryImp.SavingsAccountTransactionRepositoryImp
import com.mifos.core.data.repositoryImp.SearchRepositoryImp
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
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val ioDispatcher = named(MifosDispatchers.IO.name)


val RepositoryModule = module {
    // Auth
    single<LoginRepository> { LoginRepositoryImp(get()) }

    // Client
    single<ClientDetailsRepository> { ClientDetailsRepositoryImp(get()) }
    single<ClientListRepository> { ClientListRepositoryImp(get()) }
    single<ClientChargeRepository> { ClientChargeRepositoryImp(get()) }
    single<ClientIdentifierDialogRepository> { ClientIdentifierDialogRepositoryImp(get()) }
    single<ClientIdentifiersRepository> { ClientIdentifiersRepositoryImp(get()) }
    single<CreateNewClientRepository> { CreateNewClientRepositoryImp(get(), get(), get()) }
    single<PinPointClientRepository> { PinPointClientRepositoryImp(get()) }

    // Group
    single<CenterDetailsRepository> { CenterDetailsRepositoryImp(get(), get()) }
    single<CenterListRepository> { CenterListRepositoryImp(get()) }
    single<CreateNewCenterRepository> { CreateNewCenterRepositoryImp(get()) }

    // Group
    single<GroupDetailsRepository> { GroupDetailsRepositoryImp(get()) }
    single<GroupListRepository> { GroupListRepositoryImp(get()) }
    single<GroupLoanAccountRepository> { GroupLoanAccountRepositoryImp(get()) }
    single<CreateNewGroupRepository> { CreateNewGroupRepositoryImp(get(), get()) }
    single<GenerateCollectionSheetRepository> { GenerateCollectionSheetRepositoryImp(get(), get()) }

    // Loan
    single<LoanAccountRepository> { LoanAccountRepositoryImp(get()) }
    single<LoanAccountApprovalRepository> { LoanAccountApprovalRepositoryImp(get()) }
    single<LoanAccountDisbursementRepository> { LoanAccountDisbursementRepositoryImp(get()) }
    single<LoanAccountSummaryRepository> { LoanAccountSummaryRepositoryImp(get()) }
    single<LoanChargeDialogRepository> { LoanChargeDialogRepositoryImp(get()) }
    single<LoanChargeRepository> { LoanChargeRepositoryImp(get()) }
    single<LoanRepaymentRepository> { LoanRepaymentRepositoryImp(get()) }
    single<LoanRepaymentScheduleRepository> { LoanRepaymentScheduleRepositoryImp(get()) }
    single<LoanTransactionsRepository> { LoanTransactionsRepositoryImp(get()) }

    // Savings
    single<SavingsAccountRepository> { SavingsAccountRepositoryImp(get()) }
    single<SavingsAccountActivateRepository> { SavingsAccountActivateRepositoryImp(get()) }
    single<SavingsAccountApprovalRepository> { SavingsAccountApprovalRepositoryImp(get()) }
    single<SavingsAccountSummaryRepository> { SavingsAccountSummaryRepositoryImp(get()) }
    single<SavingsAccountTransactionRepository> { SavingsAccountTransactionRepositoryImp(get()) }

    // Sync
    single<SyncCenterPayloadsRepository> { SyncCenterPayloadsRepositoryImp(get()) }
    single<SyncCentersDialogRepository> {
        SyncCentersDialogRepositoryImp(get(), get(), get(), get(), get())
    }
    single<SyncClientPayloadsRepository> { SyncClientPayloadsRepositoryImp(get()) }
    single<SyncClientsDialogRepository> { SyncClientsDialogRepositoryImp(get(), get(), get()) }
    single<SyncGroupPayloadsRepository> { SyncGroupPayloadsRepositoryImp(get()) }
    single<SyncGroupsDialogRepository> { SyncGroupsDialogRepositoryImp(get(), get(), get(), get()) }
    single<SyncLoanRepaymentTransactionRepository> { SyncLoanRepaymentTransactionRepositoryImp(get()) }
    single<SyncSavingsAccountTransactionRepository> {
        SyncSavingsAccountTransactionRepositoryImp(
            get(),
            get(),
        )
    }

    // Others
    single<ActivateRepository> { ActivateRepositoryImp(get(), get(), get()) }
    single<ChargeDialogRepository> { ChargeDialogRepositoryImp(get()) }
    single<CheckerInboxRepository> { CheckerInboxRepositoryImp(get()) }
    single<CheckerInboxTasksRepository> { CheckerInboxTasksRepositoryImp(get()) }
    single<DataTableDataRepository> { DataTableDataRepositoryImp(get()) }
    single<DataTableListRepository> { DataTableListRepositoryImp(get(), get(), get()) }
    single<DataTableRepository> { DataTableRepositoryImp(get()) }
    single<DataTableRowDialogRepository> { DataTableRowDialogRepositoryImp(get()) }
    single<DocumentDialogRepository> { DocumentDialogRepositoryImp(get()) }
    single<DocumentListRepository> { DocumentListRepositoryImp(get()) }
    single<IndividualCollectionSheetDetailsRepository> {
        IndividualCollectionSheetDetailsRepositoryImp(get())
    }
    single<NewIndividualCollectionSheetRepository> {
        NewIndividualCollectionSheetRepositoryImp(get(), get())
    }
    single<NoteRepository> { NoteRepositoryImp(get()) }
    single<OfflineDashboardRepository> {
        OfflineDashboardRepositoryImp(get(), get(), get(), get(), get())
    }
    single<PathTrackingRepository> { PathTrackingRepositoryImp(get()) }
    single<ReportCategoryRepository> { ReportCategoryRepositoryImp(get()) }
    single<ReportDetailRepository> { ReportDetailRepositoryImp(get()) }
    single<SearchRepository> { SearchRepositoryImp(get(), get(ioDispatcher)) }
    single<SignatureRepository> { SignatureRepositoryImp(get()) }
    single<SurveyListRepository> { SurveyListRepositoryImp(get()) }
    single<SurveySubmitRepository> { SurveySubmitRepositoryImp(get()) }
}
