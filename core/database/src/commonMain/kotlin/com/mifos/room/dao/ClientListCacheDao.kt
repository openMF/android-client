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
import com.mifos.room.entities.client.ClientListCacheEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the FK-free offline-first paged client-list cache
 * ([ClientListCacheEntity], table `client_list_cache`).
 *
 * Page windows are ordered by `(page, id)` so a `LIMIT/OFFSET` read returns rows in stable
 * fetch order. The store's writer clears the page ([deleteByPage]) then re-inserts it
 * ([upsertAll], REPLACE) — mirroring the template crypto `CoinMarketDao`. [deleteAll] is the
 * logout cache-clear path (`StoreCacheManager.clearAll()`).
 */
@Dao
interface ClientListCacheDao {

    @Query("SELECT * FROM client_list_cache ORDER BY page, id LIMIT :limit OFFSET :offset")
    fun getPage(limit: Int, offset: Int): Flow<List<ClientListCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rows: List<ClientListCacheEntity>)

    @Query("DELETE FROM client_list_cache WHERE page = :page")
    suspend fun deleteByPage(page: Int)

    @Query("DELETE FROM client_list_cache")
    suspend fun deleteAll()
}
