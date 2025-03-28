/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package org.mifos.library.passcode.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.mifos.library.passcode.data.PasscodeRepository
import org.mifos.library.passcode.data.PasscodeRepositoryImpl
import org.mifos.library.passcode.utility.PreferenceManager

val PasscodeModule = module {
    single<PreferenceManager> { PreferenceManager(androidContext()) }
    single<PasscodeRepository> { PasscodeRepositoryImpl(get()) }
}
