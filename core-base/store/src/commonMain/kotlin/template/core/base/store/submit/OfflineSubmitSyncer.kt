/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package template.core.base.store.submit

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * Retries all PENDING outbox entries whenever the device regains connectivity.
 *
 * Wire this once in the app's DI scope (e.g. `AppViewModel`) to ensure that drafts saved
 * during offline submissions are automatically retried when the network comes back.
 *
 * ```kotlin
 * val syncer = coroutineScope.offlineSubmitSyncer(
 *     outbox          = roomSubmitOutbox,
 *     isOnlineFlow    = networkMonitor.isOnline,
 *     submitBlock     = { payload -> repository.submitLoanApplication(payload) },
 * )
 * syncer.start()
 * ```
 *
 * @param P Serializable payload type that the outbox holds.
 * @param R Result type returned by the server on success (ignored — fire-and-forget).
 */
class OfflineSubmitSyncer<P, R>(
    private val scope: CoroutineScope,
    private val outbox: SubmitOutbox<P>,
    private val isOnlineFlow: Flow<Boolean>,
    private val submitBlock: suspend (P) -> R,
) {

    /**
     * Begin watching [isOnlineFlow]. On each `true` emission (edge-triggered — only fires
     * when transitioning from offline to online), fetch all PENDING entries and retry each
     * one sequentially.
     *
     * Individual entry failures are logged to the outbox as FAILED; they do not abort the
     * batch — remaining entries continue to be retried.
     */
    fun start(): Job = scope.launch {
            isOnlineFlow
                .distinctUntilChanged()
                .filter { isOnline -> isOnline }
                .collect { retryAll() }
    }

    private suspend fun retryAll() {
        val pending = outbox.getAllPending()
        for (entry in pending) {
            outbox.markRetrying(entry.id)
            try {
                submitBlock(entry.payload)
                outbox.markSubmitted(entry.id)
            } catch (e: CancellationException) {
                outbox.markFailed(entry.id, "cancelled")
                throw e
            } catch (e: Exception) {
                outbox.markFailed(entry.id, e.message)
            }
        }
    }
}

/**
 * Creates an [OfflineSubmitSyncer] bound to this [CoroutineScope].
 *
 * ```kotlin
 * val syncer = viewModelScope.offlineSubmitSyncer(
 *     outbox       = roomSubmitOutbox,
 *     isOnlineFlow = networkMonitor.isOnline,
 *     submitBlock  = { payload -> api.submit(payload) },
 * )
 * syncer.start()
 * ```
 */
fun <P, R> CoroutineScope.offlineSubmitSyncer(
    outbox: SubmitOutbox<P>,
    isOnlineFlow: Flow<Boolean>,
    submitBlock: suspend (P) -> R,
): OfflineSubmitSyncer<P, R> = OfflineSubmitSyncer(this, outbox, isOnlineFlow, submitBlock)
