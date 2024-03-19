package com.mifos.feature.groupsList.di

import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.feature.groupsList.data.repositoryImp.GroupsListRepositoryImpl
import com.mifos.feature.groupsList.domain.repository.GroupsListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GroupModule {

    @Provides
    fun provideDataManagerGroups(
        dataManagerGroups: DataManagerGroups,
        prefManager: PrefManager,
    ): GroupsListRepository {
        return GroupsListRepositoryImpl(dataManagerGroups, prefManager)
    }
}