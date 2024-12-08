/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.di

import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.data.repository.LoginRepository
import com.mifos.core.data.repositoryImp.ClientDetailsRepositoryImp
import com.mifos.core.data.repositoryImp.ClientListRepositoryImp
import com.mifos.core.data.repositoryImp.LoginRepositoryImp
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
