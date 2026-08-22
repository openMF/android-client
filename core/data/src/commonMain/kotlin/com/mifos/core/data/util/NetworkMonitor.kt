/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first

interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}

/**
 * offline-first-template-migration 03-core-datastate-removal (D18/D20): [upstream] is now a
 * plain [Flow] — success values are ordinary emissions (no state-envelope wrapper to
 * special-case; "loading" is a UI/VM-layer concept per D18, not a data-layer one).
 * The network guard now works on the exception channel via [catch]: when [upstream] throws,
 * replace the error with [NetworkUnavailableException] if the device is offline, otherwise
 * rethrow the original error unchanged (so a genuine server-side failure isn't masked as a
 * connectivity issue).
 *
 * ```kotlin
 * override fun getLoans(): Flow<List<Loan>> =
 *     networkMonitor.withNetworkCheck(
 *         dataManager.getLoans()
 *     ).flowOn(ioDispatcher)
 * ```
 */
fun <T> NetworkMonitor.withNetworkCheck(upstream: Flow<T>): Flow<T> =
    upstream.catch { e ->
        if (!isOnline.first()) {
            throw NetworkUnavailableException()
        } else {
            throw e
        }
    }
