package com.mifos.core.data.di

import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.data.repository.LoginRepository
import com.mifos.core.data.repository_imp.ClientDetailsRepositoryImp
import com.mifos.core.data.repository_imp.ClientListRepositoryImp
import com.mifos.core.data.repository_imp.LoginRepositoryImp
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanger.DataManagerAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun providesClientDetailsRepository(dataManagerClient: DataManagerClient): ClientDetailsRepository =
        ClientDetailsRepositoryImp(dataManagerClient)

    @Singleton
    @Provides
    fun provideClientListRepository(dataManagerClient: DataManagerClient): ClientListRepository =
        ClientListRepositoryImp(dataManagerClient)

    @Provides
    fun providesLoginRepository(dataManagerAuth: DataManagerAuth): LoginRepository =
        LoginRepositoryImp(dataManagerAuth)
}