package com.mifos.core.data.di


import com.mifos.core.data.repository.CenterDetailsRepository
import com.mifos.core.data.repository.CenterListRepository
import com.mifos.core.data.repository.CheckerInboxRepository
import com.mifos.core.data.repository.CheckerInboxTasksRepository
import com.mifos.core.data.repository.CreateNewCenterRepository
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.data.repository.PathTrackingRepository
import com.mifos.core.data.repository.ReportCategoryRepository
import com.mifos.core.data.repository_imp.CenterDetailsRepositoryImp
import com.mifos.core.data.repository_imp.CenterListRepositoryImp
import com.mifos.core.data.repository_imp.CheckerInboxRepositoryImp
import com.mifos.core.data.repository_imp.CheckerInboxTasksRepositoryImp
import com.mifos.core.data.repository_imp.CreateNewCenterRepositoryImp
import com.mifos.core.data.repository_imp.GroupDetailsRepositoryImp
import com.mifos.core.data.repository_imp.GroupsListRepositoryImpl
import com.mifos.core.data.repository_imp.NewIndividualCollectionSheetRepositoryImp
import com.mifos.core.data.repository_imp.PathTrackingRepositoryImp
import com.mifos.core.data.repository_imp.ReportCategoryRepositoryImp
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
    internal abstract fun bindCreateNewCenterRepository(impl: CreateNewCenterRepositoryImp): CreateNewCenterRepository
}