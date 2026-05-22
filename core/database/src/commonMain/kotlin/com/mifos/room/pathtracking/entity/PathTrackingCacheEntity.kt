/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.pathtracking.entity

import template.core.base.database.Entity
import template.core.base.database.PrimaryKey

/**
 * Room cache row for a single path-tracking entry, keyed by `<userId>:<ordinal>`.
 *
 * Backs `core/data/pathtracking/store/PathTrackingListStore` — Store5 reads/writes this
 * table as the SourceOfTruth so the staff-path-tracking list survives offline + replays
 * on reconnect per RULE-STORE5-FETCH-001 (mirrors Wave 7 NoteCacheEntity).
 *
 * `UserLocation` has no server-side id; rows are persisted in fetch order via
 * [ordinal] and listed `ORDER BY ordinal ASC` to preserve the on-wire sequence. The
 * writer performs `deleteByUser(userId)` before upserting the new list, so ordinal
 * collisions across refresh runs are not possible.
 */
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "path_tracking_cache",
)
data class PathTrackingCacheEntity(
    @PrimaryKey(autoGenerate = false) val cacheKey: String,
    val userId: Int,
    val ordinal: Int,
    val staffId: Int? = null,
    val latLng: String? = null,
    val startTime: String? = null,
    val stopTime: String? = null,
    val date: String? = null,
    val startAddress: String? = null,
    val endAddress: String? = null,
    val dateFormat: String? = null,
    val locale: String? = null,
) {
    companion object {
        fun key(userId: Int, ordinal: Int): String = "$userId:$ordinal"
    }
}
