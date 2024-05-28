package com.mifos.core.data.di

import com.mifos.core.data.repository.CheckerInboxTasksRepository
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.core.data.repository_imp.CheckerInboxTasksRepositoryImp
import com.mifos.core.data.repository_imp.GroupsListRepositoryImpl
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
    internal abstract fun provideGroupListRepository(
        groupsListRepositoryImpl: GroupsListRepositoryImpl
    ): GroupsListRepository
}