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

import com.mifos.core.data.repository.ActivateRepository
import com.mifos.core.data.repository.CenterDetailsRepository
import com.mifos.core.data.repository.CenterListRepository
import com.mifos.core.data.repository.ChargeDialogRepository
import com.mifos.core.data.repository.CheckerInboxRepository
import com.mifos.core.data.repository.CheckerInboxTasksRepository
import com.mifos.core.data.repository.ClientChargeRepository
import com.mifos.core.data.repository.ClientIdentifierDialogRepository
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.data.repository.CreateNewCenterRepository
import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.data.repository.DataTableDataRepository
import com.mifos.core.data.repository.DataTableListRepository
import com.mifos.core.data.repository.DataTableRepository
import com.mifos.core.data.repository.DataTableRowDialogRepository
import com.mifos.core.data.repository.DocumentListRepository
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
import com.mifos.core.data.repository.LoanChargeDialogRepository
import com.mifos.core.data.repository.LoanChargeRepository
import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.core.data.repository.LoanRepaymentScheduleRepository
import com.mifos.core.data.repository.LoanTransactionsRepository
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.data.repository.PathTrackingRepository
import com.mifos.core.data.repository.PinPointClientRepository
import com.mifos.core.data.repository.ReportCategoryRepository
import com.mifos.core.data.repository.ReportDetailRepository
import com.mifos.core.data.repository.SearchRepository
import com.mifos.core.data.repository.SignatureRepository
import com.mifos.core.data.repository.SurveyListRepository
import com.mifos.core.data.repository.SurveySubmitRepository
import com.mifos.core.data.repositoryImp.CenterDetailsRepositoryImp
import com.mifos.core.data.repositoryImp.CenterListRepositoryImp
import com.mifos.core.data.repositoryImp.ChargeDialogRepositoryImp
import com.mifos.core.data.repositoryImp.CheckerInboxRepositoryImp
import com.mifos.core.data.repositoryImp.CheckerInboxTasksRepositoryImp
import com.mifos.core.data.repositoryImp.ClientChargeRepositoryImp
import com.mifos.core.data.repositoryImp.ClientIdentifierDialogRepositoryImp
import com.mifos.core.data.repositoryImp.CreateNewCenterRepositoryImp
import com.mifos.core.data.repositoryImp.CreateNewGroupRepositoryImp
import com.mifos.core.data.repositoryImp.DataTableListRepositoryImp
import com.mifos.core.data.repositoryImp.DataTableRepositoryImp
import com.mifos.core.data.repositoryImp.DataTableRowDialogRepositoryImp
import com.mifos.core.data.repositoryImp.DocumentListRepositoryImp
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
import com.mifos.core.data.repositoryImp.LoanChargeDialogRepositoryImp
import com.mifos.core.data.repositoryImp.LoanChargeRepositoryImp
import com.mifos.core.data.repositoryImp.LoanRepaymentRepositoryImp
import com.mifos.core.data.repositoryImp.LoanRepaymentScheduleRepositoryImp
import com.mifos.core.data.repositoryImp.LoanTransactionsRepositoryImp
import com.mifos.core.data.repositoryImp.NewIndividualCollectionSheetRepositoryImp
import com.mifos.core.data.repositoryImp.PathTrackingRepositoryImp
import com.mifos.core.data.repositoryImp.PinPointClientRepositoryImp
import com.mifos.core.data.repositoryImp.ReportCategoryRepositoryImp
import com.mifos.core.data.repositoryImp.ReportDetailRepositoryImp
import com.mifos.core.data.repositoryImp.SearchRepositoryImp
import com.mifos.core.data.repositoryImp.SignatureRepositoryImp
import com.mifos.core.data.repositoryImp.SurveyListRepositoryImp
import com.mifos.core.data.repositoryImp.SurveySubmitRepositoryImp
import com.mifos.core.data.repository_imp.ActivateRepositoryImp
import com.mifos.core.data.repository_imp.ClientIdentifiersRepositoryImp
import com.mifos.core.data.repository_imp.DataTableDataRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindCheckerInboxTasksRepository(impl: CheckerInboxTasksRepositoryImp): CheckerInboxTasksRepository

    @Binds
    abstract fun bindNewIndividualCollectionSheetRepository(impl: NewIndividualCollectionSheetRepositoryImp): NewIndividualCollectionSheetRepository

    @Binds
    internal abstract fun provideGroupListRepository(groupsListRepositoryImpl: GroupsListRepositoryImpl): GroupsListRepository

    @Binds
    internal abstract fun provideGroupDetailsRepository(impl: GroupDetailsRepositoryImp): GroupDetailsRepository

    @Binds
    internal abstract fun bindCenterListRepository(impl: CenterListRepositoryImp): CenterListRepository

    @Binds
    internal abstract fun bindCenterDetailsRepository(impl: CenterDetailsRepositoryImp): CenterDetailsRepository

    @Binds
    internal abstract fun bindCheckerInboxRepository(impl: CheckerInboxRepositoryImp): CheckerInboxRepository

    @Binds
    internal abstract fun bindReportCategoryRepository(impl: ReportCategoryRepositoryImp): ReportCategoryRepository

    @Binds
    internal abstract fun bindPathTrackingRepository(impl: PathTrackingRepositoryImp): PathTrackingRepository

    @Binds
    internal abstract fun bindClientChargeRepository(impl: ClientChargeRepositoryImp): ClientChargeRepository

    @Binds
    internal abstract fun bindCreateNewCenterRepository(impl: CreateNewCenterRepositoryImp): CreateNewCenterRepository

    @Binds
    internal abstract fun bindClientIdentifiersRepository(impl: ClientIdentifiersRepositoryImp): ClientIdentifiersRepository

    @Binds
    internal abstract fun bindCreateNewGroupRepository(impl: CreateNewGroupRepositoryImp): CreateNewGroupRepository

    @Binds
    internal abstract fun bindPinpointRepository(impl: PinPointClientRepositoryImp): PinPointClientRepository

    @Binds
    internal abstract fun bindActivateRepository(impl: ActivateRepositoryImp): ActivateRepository

    @Binds
    internal abstract fun bindReportDetailRepository(impl: ReportDetailRepositoryImp): ReportDetailRepository

    @Binds
    internal abstract fun bindLoanAccountRepository(impl: LoanAccountRepositoryImp): LoanAccountRepository

    @Binds
    internal abstract fun bindDocumentListRepository(impl: DocumentListRepositoryImp): DocumentListRepository

    @Binds
    internal abstract fun bindIndividualCollectionSheetDetailsRepositoryImp(impl: IndividualCollectionSheetDetailsRepositoryImp): IndividualCollectionSheetDetailsRepository

    @Binds
    internal abstract fun bindGroupListRepository(impl: GroupListRepositoryImp): GroupListRepository

    @Binds
    internal abstract fun bindGroupLoanAccountRepository(impl: GroupLoanAccountRepositoryImp): GroupLoanAccountRepository

    @Binds
    internal abstract fun bindLoanChargeRepository(impl: LoanChargeRepositoryImp): LoanChargeRepository

    @Binds
    internal abstract fun bindLoanChargeDialogRepository(impl: LoanChargeDialogRepositoryImp): LoanChargeDialogRepository

    @Binds
    internal abstract fun bindChargeDialogRepository(impl: ChargeDialogRepositoryImp): ChargeDialogRepository

    @Binds
    internal abstract fun bindDataTableDataRepository(impl: DataTableDataRepositoryImp): DataTableDataRepository

    @Binds
    internal abstract fun bindDataTableRowDialogRepository(impl: DataTableRowDialogRepositoryImp): DataTableRowDialogRepository

    @Binds
    internal abstract fun bindClientIdentifiersDialogRepository(impl: ClientIdentifierDialogRepositoryImp): ClientIdentifierDialogRepository

    @Binds
    internal abstract fun bindSignatureRepository(impl: SignatureRepositoryImp): SignatureRepository

    @Binds
    internal abstract fun provideSearchRepository(repository: SearchRepositoryImp): SearchRepository

    @Binds
    internal abstract fun bindGenerateCollectionSheetRepository(impl: GenerateCollectionSheetRepositoryImp): GenerateCollectionSheetRepository

    @Binds
    internal abstract fun bindSurveyListRepository(impl: SurveyListRepositoryImp): SurveyListRepository

    @Binds
    internal abstract fun bindSurveySubmitRepository(impl: SurveySubmitRepositoryImp): SurveySubmitRepository

    @Binds
    internal abstract fun bindLoanAccountApprovalRepository(impl: LoanAccountApprovalRepositoryImp): LoanAccountApprovalRepository

    @Binds
    internal abstract fun bindLoanAccountDisbursementRepository(impl: LoanAccountDisbursementRepositoryImp): LoanAccountDisbursementRepository

    @Binds
    internal abstract fun bindLoanAccountSummaryRepository(impl: LoanAccountSummaryRepositoryImp): LoanAccountSummaryRepository

    @Binds
    internal abstract fun bindLoanRepaymentRepository(impl: LoanRepaymentRepositoryImp): LoanRepaymentRepository

    @Binds
    internal abstract fun bindLoanRepaymentSchedule(impl: LoanRepaymentScheduleRepositoryImp): LoanRepaymentScheduleRepository

    @Binds
    internal abstract fun bindLoanTransactionsRepository(impl: LoanTransactionsRepositoryImp): LoanTransactionsRepository

    @Binds
    internal abstract fun bindDataTableRepository(impl: DataTableRepositoryImp): DataTableRepository

    @Binds
    internal abstract fun bindDataTableListRepository(impl: DataTableListRepositoryImp): DataTableListRepository
}
