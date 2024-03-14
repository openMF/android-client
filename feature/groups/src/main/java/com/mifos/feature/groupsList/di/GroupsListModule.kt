package com.mifos.feature.groupsList.di

import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.feature.groupsList.data.repositoryImp.GroupsListRepositoryImpl
import com.mifos.feature.groupsList.domain.repository.GroupsListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GroupsListModule {

    @Provides
    fun provideDataManagerGroups(dataManagerGroups: DataManagerGroups): GroupsListRepository {
        return GroupsListRepositoryImpl(dataManagerGroups)
    }
}