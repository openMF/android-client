package com.mifos.core.data.di

import com.mifos.core.data.repository.CheckerInboxTasksRepository
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.data.repository.SearchRepository
import com.mifos.core.data.repository_imp.CheckerInboxTasksRepositoryImp
import com.mifos.core.data.repository_imp.GroupsListRepositoryImpl
import com.mifos.core.data.repository_imp.NewIndividualCollectionSheetRepositoryImp
import com.mifos.core.data.repository_imp.SearchRepositoryImp
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
    internal abstract fun provideSearchRepository(repository: SearchRepositoryImp): SearchRepository
}