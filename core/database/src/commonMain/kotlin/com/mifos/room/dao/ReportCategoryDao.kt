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
import com.mifos.room.entities.report.ReportCategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the run-report category-list cache ([ReportCategoryEntity], table `report_categories`).
 *
 * Rows are scoped by the request tuple `(reportCategory, genericResultSet, parameterType)` — the
 * same tuple the Store key carries. Like the checker inbox (and unlike the loan ledger's incremental
 * append), the network returns the FULL category list for a given scope on every call with no
 * since-keyed delta, so the write path is a **per-key full replace** ([replaceForKey]): delete the
 * rows for that scope and re-insert the fetched rows inside one `@Transaction`, so a category that
 * dropped off the server list also drops out of the cache. The replace is atomic — a concurrent
 * [pageFlow] read for the same key never observes a half-written list.
 */
@Dao
interface ReportCategoryDao {

    @Query(
        "SELECT * FROM report_categories " +
            "WHERE reportCategory = :c AND genericResultSet = :g AND parameterType = :p",
    )
    fun pageFlow(c: String, g: Boolean, p: Boolean): Flow<List<ReportCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rows: List<ReportCategoryEntity>)

    @Transaction
    suspend fun replaceForKey(
        c: String,
        g: Boolean,
        p: Boolean,
        rows: List<ReportCategoryEntity>,
    ) {
        // Per-key full-list refresh: the network has no delta endpoint for a scope, so wipe the
        // rows for THIS key + re-insert inside one transaction. Atomic — a concurrent pageFlow read
        // for the same key never sees a partial list.
        deleteForKey(c, g, p)
        insertAll(rows)
    }

    @Query(
        "DELETE FROM report_categories " +
            "WHERE reportCategory = :c AND genericResultSet = :g AND parameterType = :p",
    )
    suspend fun deleteForKey(c: String, g: Boolean, p: Boolean)

    @Query("DELETE FROM report_categories")
    suspend fun deleteAll()
}
