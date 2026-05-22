/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.infra.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.mifos.room.infra.entity.FetchedAtEntity

/**
 * DAO for the framework-owned `framework_fetched_at` table.
 *
 * Backing store for [template.core.base.store.infra.FetchedAtRepository]. Wired in
 * `core/data` via [com.mifos.core.data.infra.impl.RoomFetchedAtRepository].
 */
@Dao
interface FetchedAtDao {

    @Query("SELECT lastFetchedMillis FROM framework_fetched_at WHERE storeKey = :storeKey")
    suspend fun read(storeKey: String): Long?

    @Upsert(entity = FetchedAtEntity::class)
    suspend fun upsert(entity: FetchedAtEntity)
}
