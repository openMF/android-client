/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.common.di

import android.content.Context
import com.mifos.core.common.utils.ConnectivityManagerNetworkMonitor
import com.mifos.core.common.utils.NetworkMonitor
import com.mifos.core.common.utils.TimeZoneBroadcastMonitor
import com.mifos.core.common.utils.TimeZoneMonitor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class AndroidPlatformDependentDataModule(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
) : PlatformDependentDataModule {
    override val networkMonitor: NetworkMonitor by lazy {
        ConnectivityManagerNetworkMonitor(context, dispatcher)
    }

    override val timeZoneMonitor: TimeZoneMonitor by lazy {
        TimeZoneBroadcastMonitor(context, scope, dispatcher)
    }
}
