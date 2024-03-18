package com.mifos.feature.client.clientDetails.di

import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.feature.client.clientDetails.data.repository_imp.ClientDetailsRepositoryImp
import com.mifos.feature.client.clientDetails.domain.repository.ClientDetailsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun providesClientDetailsRepository(dataManagerClient: DataManagerClient): ClientDetailsRepository =
        ClientDetailsRepositoryImp(dataManagerClient)


}