/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.util

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.infra.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Wraps an [upstream] [DataState] [Flow] with a reactive network guard.
 *
 * Emission priority (evaluated top-to-bottom on every pair of values):
 *  1. **[DataState.Success]**  — always forwarded; cached data survives going offline.
 *  2. **[DataState.Loading]**  — always forwarded; lets the UI render a spinner
 *                                before any network error is surfaced.
 *  3. **offline**              — emits [NetworkUnavailableException].
 *  4. **otherwise**            — forwards whatever error the upstream emitted.
 *
 * Legacy `DataState`-based seam — used only by repositories that have not yet
 * migrated to Store5. New code uses Store5 + `ScreenDataStream` instead, which
 * handles offline + captive-portal + cache-then-network natively.
 */
fun <T> NetworkMonitor.withNetworkCheck(
    upstream: Flow<DataState<T>>,
): Flow<DataState<T>> = combine(isOnline, upstream) { isOnline, dataState ->
    when {
        dataState is DataState.Success -> dataState
        dataState is DataState.Loading -> dataState
        !isOnline -> DataState.Error(NetworkUnavailableException())
        else -> dataState
    }
}
