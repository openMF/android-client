/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.di

import com.mifos.core.data.repository.LoginRepository
import com.mifos.core.domain.useCases.LoginUseCase
import com.mifos.core.domain.useCases.PasswordValidationUseCase
import com.mifos.core.domain.useCases.UsernameValidationUseCase
import com.mifos.core.domain.useCases.ValidateServerApiPathUseCase
import com.mifos.core.domain.useCases.ValidateServerEndPointUseCase
import com.mifos.core.domain.useCases.ValidateServerPortUseCase
import com.mifos.core.domain.useCases.ValidateServerProtocolUseCase
import com.mifos.core.domain.useCases.ValidateServerTenantUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideUsernameValidationUseCase(): UsernameValidationUseCase = UsernameValidationUseCase()

    @Provides
    fun providePasswordValidationUseCase(): PasswordValidationUseCase = PasswordValidationUseCase()

    @Provides
    fun provideLoginUseCase(loginRepository: LoginRepository): LoginUseCase = LoginUseCase(loginRepository)

    @Provides
    fun provideProtocolValidationUseCase() = ValidateServerProtocolUseCase()

    @Provides
    fun provideApiPathValidationUseCase() = ValidateServerApiPathUseCase()

    @Provides
    fun provideEndPointValidationUseCase() = ValidateServerEndPointUseCase()

    @Provides
    fun providePortValidationUseCase() = ValidateServerPortUseCase()

    @Provides
    fun provideTenantValidationUseCase() = ValidateServerTenantUseCase()
}
