/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.di

import com.mifos.core.data.util.WasmJsPlatformDependentDataModule
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<PlatformDependentDataModule> { getPlatformDataModule }
    }
actual val getPlatformDataModule: PlatformDependentDataModule
    get() = WasmJsPlatformDependentDataModule()
