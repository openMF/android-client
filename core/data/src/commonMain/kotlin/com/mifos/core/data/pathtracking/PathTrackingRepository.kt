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

import com.mifos.core.model.objects.users.UserLocation

/**
 * Per-feature path-tracking repository (Phase C Wave 8 of store5-adoption).
 *
 * Modern suspend-only contract. All methods throw on HTTP failure or transport
 * error — callers wrap mutations in `SubmitHandler.submit { ... }` and wrap
 * reads in explicit try/catch (re-throwing `CancellationException`) to drive
 * `ScreenState<T>`. No legacy `DataState<*>` / `Flow<DataState<*>>`.
 *
 * Path-tracking entries are **not** Store5-cached: they are user-scoped lists
 * fetched on-demand when the officer opens the "Track my path" screen, with a
 * pull-to-refresh affordance for explicit refetches. There is no useful offline
 * value to memoizing the list, and the staff-path-tracking POST is a per-second
 * geolocation appender — a Store5 read pipeline on top would only add latency.
 * See RULE-STORE5-FETCH-001 — direct suspend reads + try/catch → `ScreenState`
 * is the documented exception. Same call site shape as `NoteRepository`
 * (Wave 7).
 */
interface PathTrackingRepository {

    /**
     * Retrieve the saved track entries for the staff member with id [userId].
     * Throws on HTTP failure or transport error.
     */
    suspend fun getUserPathTracking(userId: Int): List<UserLocation>

    /**
     * Append a new track entry for the staff member with id [userId]. Throws on
     * HTTP failure or transport error.
     */
    suspend fun addUserPathTracking(
        userId: Int,
        userLocation: UserLocation,
    )
}
