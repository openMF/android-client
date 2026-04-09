/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifos.feature.passcode.di

import com.mifos.feature.passcode.biometricsSetup.BiometricSetupScreenViewmodel
import com.mifos.feature.passcode.mifosPasscode.MifosPasscodeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val MifosAuthenticatorModule = module {
    viewModelOf(::BiometricSetupScreenViewmodel)
    viewModelOf(::MifosPasscodeViewModel)
}
