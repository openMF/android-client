package com.mifos.feature.groupsList.di

import com.mifos.feature.groupsList.data.repositoryImp.GroupsListRepositoryImpl
import com.mifos.feature.groupsList.domain.repository.GroupsListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GroupModule {

    @Binds
    internal abstract fun provideGroupListRepository(
        groupsListRepositoryImpl: GroupsListRepositoryImpl
    ): GroupsListRepository
}