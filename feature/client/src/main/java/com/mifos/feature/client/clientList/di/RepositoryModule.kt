package com.mifos.feature.client.clientList.di

import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.feature.client.clientList.data.repositoryImp.ClientListRepositoryImp
import com.mifos.feature.client.clientList.domain.repository.ClientListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideClientListRepository(dataManagerClient: DataManagerClient): ClientListRepository =
        ClientListRepositoryImp(dataManagerClient)

}