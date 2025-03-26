/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.testing.di

import com.mifos.core.common.network.MifosDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher
import org.koin.core.qualifier.named
import org.koin.dsl.module

val TestDispatchersModule = module {
    single<CoroutineDispatcher>(named(MifosDispatchers.IO)) { get<TestDispatcher>() }
    single<CoroutineDispatcher>(named(MifosDispatchers.Default)) { get<TestDispatcher>() }
}
