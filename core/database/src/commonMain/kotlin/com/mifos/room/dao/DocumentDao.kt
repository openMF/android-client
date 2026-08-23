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
import com.mifos.room.entities.document.DocumentEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the document-metadata cache ([DocumentEntity], table `documents`).
 *
 * The table caches many parents' document lists side by side, scoped by the composite
 * `(parentEntityType, parentEntityId)`. Fineract's documents endpoint has no
 * incremental/since-keyed delta — it always returns the FULL current list for a parent — so
 * the write path is a **full-list replace per parent** ([replaceForParent]): wipe just that
 * parent's rows and re-insert the fetched list inside one `@Transaction`, so a document that
 * was removed on the server also drops out of the cache while OTHER parents' cached lists are
 * left untouched. The replace is atomic so a concurrent [pageFlow] read never observes a
 * half-written list.
 */
@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents WHERE parentEntityType = :type AND parentEntityId = :id")
    fun pageFlow(type: String, id: Int): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rows: List<DocumentEntity>)

    @Transaction
    suspend fun replaceForParent(type: String, id: Int, rows: List<DocumentEntity>) {
        // Full-list refresh scoped to one parent: the network has no delta endpoint, so wipe
        // just this parent's rows + re-insert inside one transaction. Atomic — a concurrent
        // pageFlow read never sees a partial list, and other parents' caches are untouched.
        deleteForParent(type, id)
        insertAll(rows)
    }

    @Query("DELETE FROM documents WHERE parentEntityType = :type AND parentEntityId = :id")
    suspend fun deleteForParent(type: String, id: Int)

    @Query("DELETE FROM documents")
    suspend fun deleteAll()
}
