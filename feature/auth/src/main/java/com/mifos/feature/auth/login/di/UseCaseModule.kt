package com.mifos.feature.auth.login.di

import com.mifos.feature.auth.login.domain.repository.LoginRepository
import com.mifos.feature.auth.login.domain.use_case.LoginUseCase
import com.mifos.feature.auth.login.domain.use_case.PasswordValidationUseCase
import com.mifos.feature.auth.login.domain.use_case.UsernameValidationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideUsernameValidationUseCase(): UsernameValidationUseCase =
        UsernameValidationUseCase()

    @Provides
    fun providePasswordValidationUseCase(): PasswordValidationUseCase =
        PasswordValidationUseCase()

    @Provides
    fun provideLoginUseCase(loginRepository: LoginRepository): LoginUseCase =
        LoginUseCase(loginRepository)

}