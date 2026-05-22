/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.pathtracking.mapper

import com.mifos.core.model.objects.users.UserLocation
import com.mifos.room.pathtracking.entity.PathTrackingCacheEntity

/**
 * Domain ↔ Entity mapping for [UserLocation]. Used by `PathTrackingListStore`'s
 * SourceOfTruth.
 *
 * `UserLocation` has no server-side id, so [ordinal] (the index of the row inside
 * the wholesale-fetched list) provides the stable key. The writer is expected to
 * call `deleteByUser(userId)` before upserting so ordinal collisions cannot occur.
 */
fun UserLocation.toEntity(userId: Int, ordinal: Int): PathTrackingCacheEntity =
    PathTrackingCacheEntity(
        cacheKey = PathTrackingCacheEntity.key(userId, ordinal),
        userId = userId,
        ordinal = ordinal,
        staffId = staffId,
        latLng = latLng,
        startTime = startTime,
        stopTime = stopTime,
        date = date,
        startAddress = startAddress,
        endAddress = endAddress,
        dateFormat = dateFormat,
        locale = locale,
    )

fun PathTrackingCacheEntity.toDomain(): UserLocation =
    UserLocation(
        staffId = staffId,
        latLng = latLng,
        startTime = startTime,
        stopTime = stopTime,
        date = date,
        startAddress = startAddress,
        endAddress = endAddress,
        dateFormat = dateFormat,
        locale = locale,
    )
