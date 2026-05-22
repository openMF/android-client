/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.pathtracking.store

import kotlinx.serialization.Serializable

/**
 * Composite key for `PathTrackingListStore` — identifies one staff member's
 * tracked-path list. One Store5 cache row per [userId].
 */
@Serializable
data class PathTrackingListKey(
    val userId: Int,
)

/** Stable cache key for `FetchedAtRepository` indexing. */
fun PathTrackingListKey.cacheKey(): String = "pathTracking:list:$userId"
