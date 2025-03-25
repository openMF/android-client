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

import com.mifos.core.domain.useCases.LoginUseCase
import com.mifos.core.domain.useCases.PasswordValidationUseCase
import com.mifos.core.domain.useCases.UsernameValidationUseCase
import com.mifos.core.domain.useCases.ValidateServerApiPathUseCase
import com.mifos.core.domain.useCases.ValidateServerEndPointUseCase
import com.mifos.core.domain.useCases.ValidateServerPortUseCase
import com.mifos.core.domain.useCases.ValidateServerProtocolUseCase
import com.mifos.core.domain.useCases.ValidateServerTenantUseCase
import org.koin.dsl.module

val UseCaseModule = module {
    single<UsernameValidationUseCase> { UsernameValidationUseCase() }
    single<PasswordValidationUseCase> { PasswordValidationUseCase() }
    single<LoginUseCase> { LoginUseCase(get()) }
    single<ValidateServerProtocolUseCase> { ValidateServerProtocolUseCase() }
    single<ValidateServerApiPathUseCase> { ValidateServerApiPathUseCase() }
    single<ValidateServerEndPointUseCase> { ValidateServerEndPointUseCase() }
    single<ValidateServerPortUseCase> { ValidateServerPortUseCase() }
    single<ValidateServerTenantUseCase> { ValidateServerTenantUseCase() }
}