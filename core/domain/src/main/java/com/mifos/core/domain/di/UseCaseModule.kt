package com.mifos.core.domain.di

import com.mifos.core.data.repository.LoginRepository
import com.mifos.core.domain.use_cases.LoginUseCase
import com.mifos.core.domain.use_cases.PasswordValidationUseCase
import com.mifos.core.domain.use_cases.UsernameValidationUseCase
import com.mifos.core.domain.use_cases.ValidateServerApiPathUseCase
import com.mifos.core.domain.use_cases.ValidateServerEndPointUseCase
import com.mifos.core.domain.use_cases.ValidateServerPortUseCase
import com.mifos.core.domain.use_cases.ValidateServerProtocolUseCase
import com.mifos.core.domain.use_cases.ValidateServerTenantUseCase
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