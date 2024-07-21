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
import com.mifos.core.data.repository.DataTableDataRepository
import com.mifos.core.data.repository.DataTableRowDialogRepository
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.data.repository.GroupListRepository
import com.mifos.core.data.repository.GroupLoanAccountRepository
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.core.data.repository.IndividualCollectionSheetDetailsRepository
import com.mifos.core.data.repository.LoanAccountRepository
import com.mifos.core.data.repository.LoanChargeDialogRepository
import com.mifos.core.data.repository.LoanChargeRepository
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.data.repository.PathTrackingRepository
import com.mifos.core.data.repository.PinPointClientRepository
import com.mifos.core.data.repository.ReportCategoryRepository
import com.mifos.core.data.repository.ReportDetailRepository
import com.mifos.core.data.repository_imp.ActivateRepositoryImp
import com.mifos.core.data.repository_imp.CenterDetailsRepositoryImp
import com.mifos.core.data.repository_imp.CenterListRepositoryImp
import com.mifos.core.data.repository_imp.ChargeDialogRepositoryImp
import com.mifos.core.data.repository_imp.CheckerInboxRepositoryImp
import com.mifos.core.data.repository_imp.CheckerInboxTasksRepositoryImp
import com.mifos.core.data.repository_imp.ClientChargeRepositoryImp
import com.mifos.core.data.repository_imp.ClientIdentifierDialogRepositoryImp
import com.mifos.core.data.repository_imp.ClientIdentifiersRepositoryImp
import com.mifos.core.data.repository_imp.CreateNewCenterRepositoryImp
import com.mifos.core.data.repository_imp.DataTableDataRepositoryImp
import com.mifos.core.data.repository_imp.DataTableRowDialogRepositoryImp
import com.mifos.core.data.repository_imp.DocumentListRepositoryImp
import com.mifos.core.data.repository_imp.GroupDetailsRepositoryImp
import com.mifos.core.data.repository_imp.GroupListRepositoryImp
import com.mifos.core.data.repository_imp.GroupLoanAccountRepositoryImp
import com.mifos.core.data.repository_imp.GroupsListRepositoryImpl
import com.mifos.core.data.repository_imp.IndividualCollectionSheetDetailsRepositoryImp
import com.mifos.core.data.repository_imp.LoanAccountRepositoryImp
import com.mifos.core.data.repository_imp.LoanChargeDialogRepositoryImp
import com.mifos.core.data.repository_imp.LoanChargeRepositoryImp
import com.mifos.core.data.repository_imp.NewIndividualCollectionSheetRepositoryImp
import com.mifos.core.data.repository_imp.PathTrackingRepositoryImp
import com.mifos.core.data.repository_imp.PinPointClientRepositoryImp
import com.mifos.core.data.repository_imp.ReportCategoryRepositoryImp
import com.mifos.core.data.repository_imp.ReportDetailRepositoryImp
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
    internal abstract fun provideGroupListRepository(
        groupsListRepositoryImpl: GroupsListRepositoryImpl
    ): GroupsListRepository

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
}