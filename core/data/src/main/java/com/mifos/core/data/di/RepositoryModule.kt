package com.mifos.core.data.di

import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.repository_imp.ClientDetailsRepositoryImp
import com.mifos.core.network.datamanager.DataManagerClient
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