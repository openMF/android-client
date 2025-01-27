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

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.mifos.library.passcode.data.PasscodeManager
import org.mifos.library.passcode.data.PasscodeRepository
import org.mifos.library.passcode.data.PasscodeRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun providePasscodeManager(@ApplicationContext context: Context): PasscodeManager {
        return PasscodeManager(context)
    }

    @Provides
    @Singleton
    fun providesPasscodeRepository(preferenceManager: PasscodeManager): PasscodeRepository {
        return PasscodeRepositoryImpl(preferenceManager)
    }
}
