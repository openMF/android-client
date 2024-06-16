package com.mifos.core.data.di

import com.mifos.core.data.repository.CenterDetailsRepository
import com.mifos.core.data.repository.CenterListRepository
import com.mifos.core.data.repository.CheckerInboxTasksRepository
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.data.repository_imp.CenterDetailsRepositoryImp
import com.mifos.core.data.repository_imp.CenterListRepositoryImp
import com.mifos.core.data.repository_imp.CheckerInboxTasksRepositoryImp
import com.mifos.core.data.repository_imp.GroupDetailsRepositoryImp
import com.mifos.core.data.repository_imp.GroupsListRepositoryImpl
import com.mifos.core.data.repository_imp.NewIndividualCollectionSheetRepositoryImp
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
}