/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.mifos.room.entities.group.CenterListCacheEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the FK-free offline-first paged center-list cache
 * ([CenterListCacheEntity], table `center_list_cache`).
 *
 * Page windows are ordered by `(page, id)` so a `LIMIT/OFFSET` read returns rows in stable
 * fetch order. The store's writer clears the page ([deleteByPage]) then re-inserts it
 * ([upsertAll], REPLACE) — mirroring the client-list `ClientListCacheDao` and group-list
 * `GroupListCacheDao`. [deleteAll] is the logout cache-clear path (`StoreCacheManager.clearAll()`).
 */
@Dao
interface CenterListCacheDao {

    @Query("SELECT * FROM center_list_cache ORDER BY page, id LIMIT :limit OFFSET :offset")
    fun getPage(limit: Int, offset: Int): Flow<List<CenterListCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rows: List<CenterListCacheEntity>)

    @Query("DELETE FROM center_list_cache WHERE page = :page")
    suspend fun deleteByPage(page: Int)

    @Query("DELETE FROM center_list_cache")
    suspend fun deleteAll()
}
