/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.note.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.mifos.room.note.entity.NoteCacheEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the `note_cache` table — backs the `NoteListStore` Store5 SourceOfTruth.
 */
@Dao
interface NoteCacheDao {

    @Upsert(entity = NoteCacheEntity::class)
    suspend fun upsertAll(entities: List<NoteCacheEntity>)

    @Query("SELECT * FROM note_cache WHERE resourceType = :resourceType AND resourceId = :resourceId ORDER BY createdOn DESC")
    fun observeByResource(resourceType: String, resourceId: Long): Flow<List<NoteCacheEntity>>

    @Query("DELETE FROM note_cache WHERE resourceType = :resourceType AND resourceId = :resourceId")
    suspend fun deleteByResource(resourceType: String, resourceId: Long)

    @Query("DELETE FROM note_cache WHERE cacheKey = :cacheKey")
    suspend fun deleteByKey(cacheKey: String)

    @Query("DELETE FROM note_cache")
    suspend fun deleteAll()
}
