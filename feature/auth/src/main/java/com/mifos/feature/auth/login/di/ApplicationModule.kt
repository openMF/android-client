package com.mifos.feature.auth.login.di

import com.mifos.core.network.datamanger.DataManagerAuth
import com.mifos.feature.auth.login.data.repository_imp.LoginRepositoryImp
import com.mifos.feature.auth.login.domain.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object ApplicationModule {

    @Provides
    fun providesLoginRepository(dataManagerAuth: DataManagerAuth): LoginRepository =
        LoginRepositoryImp(dataManagerAuth)

}