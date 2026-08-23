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
import com.mifos.room.entities.checkerinbox.CheckerTaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the checker-inbox task cache ([CheckerTaskEntity], table `checker_tasks`).
 *
 * Unlike the loan-transaction ledger (incremental append), the checker inbox has no
 * incremental/since-keyed endpoint — the network always returns the FULL current task
 * list. So the write path is a **full-list replace** ([replaceAll]): wipe the table and
 * re-insert the fetched rows inside one `@Transaction`, so a stale approved/rejected task
 * that dropped off the server list also drops out of the cache. The replace is atomic so a
 * concurrent read never observes a half-written list.
 */
@Dao
interface CheckerTaskDao {

    @Query("SELECT * FROM checker_tasks")
    fun pageFlow(): Flow<List<CheckerTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rows: List<CheckerTaskEntity>)

    @Transaction
    suspend fun replaceAll(rows: List<CheckerTaskEntity>) {
        // Full-list refresh: the network has no delta endpoint, so wipe + re-insert inside
        // one transaction. Atomic — a concurrent pageFlow read never sees a partial list.
        deleteAll()
        insertAll(rows)
    }

    @Query("DELETE FROM checker_tasks")
    suspend fun deleteAll()
}
