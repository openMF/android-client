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

import com.mifos.room.infra.entity.BookkeeperEntity
import template.core.base.database.Dao
import template.core.base.database.Query
import template.core.base.database.Upsert

/**
 * DAO for [BookkeeperEntity]. Provides persistent sync-failure tracking
 * for [org.mobilenativefoundation.store.store5.Bookkeeper] implementations.
 */
@Dao
interface BookkeeperDao {

    @Query("SELECT lastFailedSync FROM store_bookkeeper WHERE `key` = :key")
    suspend fun getLastFailedSync(key: String): Long?

    @Upsert
    suspend fun upsert(entity: BookkeeperEntity)

    @Query("DELETE FROM store_bookkeeper WHERE `key` = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM store_bookkeeper")
    suspend fun deleteAll()
}
