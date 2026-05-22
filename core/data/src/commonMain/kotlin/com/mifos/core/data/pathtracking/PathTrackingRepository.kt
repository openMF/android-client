/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.pathtracking

import com.mifos.core.data.pathtracking.store.PathTrackingListKey
import com.mifos.core.model.objects.users.UserLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import template.core.base.store.screen.ScreenDataStream

/**
 * Per-feature path-tracking repository (Phase C Wave 11 of store5-adoption).
 *
 * Offline-first per RULE-STORE5-FETCH-001:
 * - **Reads** ([pathTrackingStream]) flow through Store5 — Room is the SourceOfTruth,
 *   Ktorfit is the Fetcher, `DecisionEngine` resolves cache-then-network +
 *   auto-refresh on reconnect. A field officer's tracked-path history is
 *   offline-viewable after the first online fetch.
 * - **Mutations** ([addUserPathTracking]) are suspend writes against the server;
 *   the impl invalidates the matching list cache after success so the new entry
 *   appears in [pathTrackingStream] without a manual reload.
 */
interface PathTrackingRepository {

    /**
     * Reactive stream of the tracked-path list for [keyFlow]. Re-streams on key
     * change. Wraps the underlying `Store<PathTrackingListKey, List<UserLocation>>`
     * via `asScreenStream` so the screen gets `Loading / Empty / Error /
     * NoNetwork / Content` slots out-of-box plus auto-refresh on reconnect.
     */
    fun pathTrackingStream(
        keyFlow: Flow<PathTrackingListKey>,
        scope: CoroutineScope,
    ): ScreenDataStream<List<UserLocation>>

    /**
     * Append a new tracked-path entry for the staff member with id [userId].
     * After success, invalidates the matching list cache so the new entry
     * appears in [pathTrackingStream]. Throws on HTTP failure or transport
     * error.
     */
    suspend fun addUserPathTracking(
        userId: Int,
        userLocation: UserLocation,
    )
}
