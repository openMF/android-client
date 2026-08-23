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
import androidx.room3.Transaction
import com.mifos.room.entities.noncore.NoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the note cache ([NoteEntity], table `Note`).
 *
 * The single `Note` table serves many parents (a client, a loan, a savings account, ...), so
 * every read/write is scoped by the composite `(entityType, entityId)` the note list belongs
 * to. Like the checker-inbox cache, Fineract's notes endpoint returns the FULL current list
 * for a parent on every call (no since-keyed delta), so the write path is a **full-list
 * replace per parent** ([replaceForParent]): wipe just that parent's rows and re-insert the
 * fetched rows inside one `@Transaction`, so a note deleted server-side also drops out of the
 * cache. The replace is atomic — a concurrent [pageFlow] read never observes a half-written
 * list, and it only ever touches the one parent's rows (never another parent's cache).
 */
@Dao
interface NoteDao {

    @Query("SELECT * FROM Note WHERE entityType = :type AND entityId = :id")
    fun pageFlow(type: String, id: Long): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rows: List<NoteEntity>)

    @Query("DELETE FROM Note WHERE entityType = :type AND entityId = :id")
    suspend fun deleteForParent(type: String, id: Long)

    @Transaction
    suspend fun replaceForParent(type: String, id: Long, rows: List<NoteEntity>) {
        // Full-list refresh for one parent: the network has no delta endpoint, so wipe just
        // this parent's rows + re-insert inside one transaction. Atomic — a concurrent
        // pageFlow(type, id) read never sees a partial list; other parents are untouched.
        deleteForParent(type, id)
        insertAll(rows)
    }

    @Query("DELETE FROM Note")
    suspend fun deleteAll()
}
