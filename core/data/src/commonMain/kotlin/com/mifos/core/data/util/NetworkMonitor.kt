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

import com.mifos.core.common.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

// ═════════════════════════════════════════════════════════════════════════════
//  NetworkUnavailableException
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Thrown when a network operation is attempted without a stable, validated
 * internet connection.
 *
 * @param message Human-readable reason shown in logs and crash reports.
 * @param cause   Optional underlying throwable.
 */
class NetworkUnavailableException(
    message: String = "No stable network connection available",
    cause: Throwable? = null,
) : IllegalStateException(message, cause)

// ═════════════════════════════════════════════════════════════════════════════
//  NetworkMonitor
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Observes the device's internet connectivity state.
 *
 * The concrete Android implementation must:
 *  - Emit the current state immediately on collection (no cold-start gap).
 *  - Apply [distinctUntilChanged] so downstream only reacts to real changes.
 *  - Unregister the system callback inside [awaitClose] to prevent leaks.
 */
interface NetworkMonitor {
    /** Emits `true` when the device has a validated connection, `false` otherwise. */
    val isOnline: Flow<Boolean>
}

// ═════════════════════════════════════════════════════════════════════════════
//  NetworkMonitor — extensions
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Snapshots the current network state without keeping the [Flow] alive.
 *
 * Calls [Flow.first] internally — the subscription opens, reads one value,
 * then cancels immediately.
 *
 * Use inside `suspend` functions for one-shot operations (POST, PUT, DELETE)
 * where you just need a yes/no before proceeding.
 * For streaming [Flow] pipelines use [withNetworkCheck] instead.
 *
 * ```kotlin
 * if (!networkMonitor.requireOnline()) {
 *     return DataState.Error(NetworkUnavailableException())
 * }
 * ```
 */
suspend fun NetworkMonitor.requireOnline(): Boolean = isOnline.first()

/**
 * Wraps an [upstream] [DataState] [Flow] with a reactive network guard.
 *
 * Uses [combine] so the merge function re-runs whenever *either* [isOnline]
 * **or** [upstream] emits a new value.
 *
 * Emission priority (evaluated top-to-bottom):
 *  1. **[DataState.Success]**  — always forwarded; cached data survives going offline.
 *  2. **[DataState.Loading]**  — always forwarded; lets the UI show a spinner
 *                                before any error is surfaced.
 *  3. **offline**              — emits [NetworkUnavailableException].
 *  4. **otherwise**            — forwards whatever error the upstream emitted.
 *
 * ```kotlin
 * override fun getLoans(): Flow<DataState<List<Loan>>> =
 *     networkMonitor.withNetworkCheck(
 *         dataManager.getLoans().asDataStateFlow()
 *     ).flowOn(ioDispatcher)
 * ```
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
