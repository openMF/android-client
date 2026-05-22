/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.pathtracking.dao

import com.mifos.room.pathtracking.entity.PathTrackingCacheEntity
import kotlinx.coroutines.flow.Flow
import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert

/**
 * DAO for the `path_tracking_cache` table — backs `PathTrackingListStore`'s
 * SourceOfTruth.
 */
@Dao
interface PathTrackingCacheDao {

    @Upsert(entity = PathTrackingCacheEntity::class)
    suspend fun upsertAll(entities: List<PathTrackingCacheEntity>)

    @Query("SELECT * FROM path_tracking_cache WHERE userId = :userId ORDER BY ordinal ASC")
    fun observeByUser(userId: Int): Flow<List<PathTrackingCacheEntity>>

    @Query("DELETE FROM path_tracking_cache WHERE userId = :userId")
    suspend fun deleteByUser(userId: Int)

    @Query("DELETE FROM path_tracking_cache")
    suspend fun deleteAll()
}
