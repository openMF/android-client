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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * offline-first-template-migration 03-core-datastate-removal (D18/D20): plain suspend-call
 * helper — errors surface via the normal Kotlin exception channel (caught by the `Flow`'s
 * collector / `AppErrorMapper` at the `core/data` repo boundary or the ViewModel), instead of a
 * hand-rolled result-envelope wrapper. Renamed from the pre-migration helper (which built that
 * envelope) so every call site across `repositoryImp` — `return runSuspendCall(ioDispatcher)
 * { ... }` — now returns the plain `T` those repos' `Flow<T>`/`suspend fun T` signatures need.
 *
 * @param context Optional [CoroutineDispatcher] to switch context for the block execution.
 *                 If provided, the block is executed with [withContext].
 * @param block The suspend lambda that performs the actual work.
 * @return The block's result directly; exceptions propagate to the caller.
 */
suspend fun <T> runSuspendCall(
    context: CoroutineDispatcher? = null,
    block: suspend () -> T,
): T =
    if (context != null) {
        withContext(context) { block() }
    } else {
        block()
    }

/**
 * Runs a suspend function with a network connectivity check.
 *
 * This overload ensures the device is online before executing the block.
 * If offline, throws [NetworkUnavailableException].
 * If online, delegates to the basic [runSuspendCall] for execution.
 *
 * @param networkMonitor The [NetworkMonitor] to check network status.
 * @param context Optional [CoroutineDispatcher] to switch context for the block execution.
 * @param block The suspend lambda that performs the actual work.
 * @return The block's result; throws [NetworkUnavailableException] if offline.
 */
suspend fun <T> runSuspendCall(
    networkMonitor: NetworkMonitor,
    context: CoroutineDispatcher? = null,
    block: suspend () -> T,
): T {
    if (!networkMonitor.isOnline.first()) {
        throw NetworkUnavailableException()
    }
    return runSuspendCall(context, block)
}
