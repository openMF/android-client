/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.di

import com.mifos.core.data.util.NetworkMonitor
import org.koin.core.module.Module

interface PlatformDependentDataModule {
    val networkMonitor: NetworkMonitor
}

expect val platformModule: Module

expect val getPlatformDataModule: PlatformDependentDataModule
