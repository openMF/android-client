/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.legacy.di

// REFERENCE-ONLY. This file lives inside `core/data/.../legacy/` which is
// gradle-excluded from compilation (kotlin.exclude("**/legacy/**")). It is
// preserved as the canonical record of the 55 Koin bindings that were active
// before T0.1 quarantine moved their Impls to `core/data/.../legacy/repositoryImp/`
// and `core/data/.../legacy/syncsurvey/impl/`.
//
// Per-feature waves (Tier 1) restore each feature's slice of these bindings into
// the live RepositoryModule (or into the feature's own DI module if the new
// architecture moves them there). When all features are migrated, this file is
// deleted as part of the Tier 2 closeout — see active/feature-vertical-migration/PLAN.md
// "Closeout" section.
//
// The bindings below DO NOT COMPILE. The Impl classes still exist in legacy/
// but the legacy/ dir is gradle-excluded, so their symbols aren't visible to
// any active Kotlin compilation unit. Don't try to fix the imports — they are
// intentionally orphan references for human + tooling readability only.

/*
import com.mifos.core.data.legacy.repositoryImp.AmountTransferRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.CenterDetailsRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.CenterListRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.ChargeRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.CheckerInboxRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.CheckerInboxTasksRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.ClientDetailsEditRepositoryImpl
import com.mifos.core.data.legacy.repositoryImp.ClientDetailsRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.ClientIdentifiersRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.ClientListRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.CreateNewCenterRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.CreateNewClientRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.CreateNewGroupRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.DataTableDataRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.DataTableListRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.DataTableRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.DataTableRowDialogRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.DocumentCreateUpdateRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.DocumentListRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.FixedDepositRepositoryImpl
import com.mifos.core.data.legacy.repositoryImp.GenerateCollectionSheetRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.GroupDetailsRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.GroupsListRepositoryImpl
import com.mifos.core.data.legacy.repositoryImp.IndividualCollectionSheetDetailsRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.LoanAccountDisbursementRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.LoanAccountRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.LoanAccountSummaryRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.LoanChargeFormRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.LoanRepaymentRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.LoanReschedulesRepositoryImpl
import com.mifos.core.data.legacy.repositoryImp.NewIndividualCollectionSheetRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.OfflineDashboardRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.PinPointClientRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.RecurringAccountRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.ReportCategoryRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.ReportDetailRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SavingsAccountActivateRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SavingsAccountApprovalRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SavingsAccountRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SavingsAccountSummaryRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SavingsAccountTransactionReceiptRepositoryImpl
import com.mifos.core.data.legacy.repositoryImp.SavingsAccountTransactionRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.ShareAccountRepositoryImpl
import com.mifos.core.data.legacy.repositoryImp.SignatureRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SurveyListRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SurveySubmitRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SyncCenterPayloadsRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SyncCentersDialogRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SyncClientPayloadsRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SyncClientsDialogRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SyncGroupPayloadsRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SyncGroupsDialogRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SyncLoanRepaymentTransactionRepositoryImp
import com.mifos.core.data.legacy.repositoryImp.SyncSavingsAccountTransactionRepositoryImp
import com.mifos.core.data.legacy.syncsurvey.impl.SyncSurveysDialogRepositoryImpl

import com.mifos.core.data.repository.AmountTransferRepository
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
import com.mifos.core.data.repository.DocumentCreateUpdateRepository
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.data.repository.FixedDepositRepository
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.core.data.repository.IndividualCollectionSheetDetailsRepository
import com.mifos.core.data.repository.LoanAccountDisbursementRepository
import com.mifos.core.data.repository.LoanAccountRepository
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.repository.LoanChargeFormRepository
import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.core.data.repository.LoanReschedulesRepository
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.data.repository.OfflineDashboardRepository
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
import com.mifos.core.data.syncsurvey.SyncSurveysDialogRepository

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val LegacyRepositoryModule = module {

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
    singleOf(::CreateNewGroupRepositoryImp) bind CreateNewGroupRepository::class

    // Loan
    singleOf(::LoanAccountRepositoryImp) bind LoanAccountRepository::class
    singleOf(::LoanAccountDisbursementRepositoryImp) bind LoanAccountDisbursementRepository::class
    singleOf(::LoanAccountSummaryRepositoryImp) bind LoanAccountSummaryRepository::class
    singleOf(::LoanChargeFormRepositoryImp) bind LoanChargeFormRepository::class
    singleOf(::LoanRepaymentRepositoryImp) bind LoanRepaymentRepository::class
    singleOf(::LoanReschedulesRepositoryImpl) bind LoanReschedulesRepository::class

    // Amount Transfer
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

    // Checker inbox + data table + document + collection sheet
    singleOf(::CheckerInboxRepositoryImp) bind CheckerInboxRepository::class
    singleOf(::CheckerInboxTasksRepositoryImp) bind CheckerInboxTasksRepository::class
    singleOf(::DataTableDataRepositoryImp) bind DataTableDataRepository::class
    singleOf(::DataTableListRepositoryImp) bind DataTableListRepository::class
    singleOf(::DataTableRepositoryImp) bind DataTableRepository::class
    singleOf(::DataTableRowDialogRepositoryImp) bind DataTableRowDialogRepository::class
    singleOf(::DocumentCreateUpdateRepositoryImp) bind DocumentCreateUpdateRepository::class
    singleOf(::DocumentListRepositoryImp) bind DocumentListRepository::class
    singleOf(::IndividualCollectionSheetDetailsRepositoryImp) bind IndividualCollectionSheetDetailsRepository::class
    singleOf(::NewIndividualCollectionSheetRepositoryImp) bind NewIndividualCollectionSheetRepository::class
    singleOf(::GenerateCollectionSheetRepositoryImp) bind GenerateCollectionSheetRepository::class

    // Offline + reports + signature + survey
    singleOf(::OfflineDashboardRepositoryImp) bind OfflineDashboardRepository::class
    singleOf(::ReportCategoryRepositoryImp) bind ReportCategoryRepository::class
    singleOf(::ReportDetailRepositoryImp) bind ReportDetailRepository::class
    singleOf(::SignatureRepositoryImp) bind SignatureRepository::class
    singleOf(::SurveyListRepositoryImp) bind SurveyListRepository::class
    singleOf(::SurveySubmitRepositoryImp) bind SurveySubmitRepository::class

    // Recurring + share + fixed deposit
    singleOf(::RecurringAccountRepositoryImp) bind RecurringAccountRepository::class
    singleOf(::ShareAccountRepositoryImpl) bind ShareAccountRepository::class
    singleOf(::FixedDepositRepositoryImpl) bind FixedDepositRepository::class
}
*/
