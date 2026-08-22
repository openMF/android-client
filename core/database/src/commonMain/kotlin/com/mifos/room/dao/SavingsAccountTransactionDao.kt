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
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the savings-account transaction ledger, scoped by `savingsAccountId` against
 * the EXISTING [SavingsAccountTransactionEntity] (table `TransactionTable`). No new
 * entity is introduced — the savings ledger rows already live in `TransactionTable`,
 * populated from the same `?associations=transactions` full-fetch pattern as loans
 * (see `SavingsAccountTransactionStore`).
 *
 * Same insert-only-new (S5-PAGE-ATOMIC) write path as [LoanTransactionDao]: the full
 * nested list from the network is appended inside a single `@Transaction` with
 * conflict-IGNORE, never wiped and re-inserted.
 */
@Dao
interface SavingsAccountTransactionDao {

    @Query("SELECT MAX(id) FROM TransactionTable WHERE savingsAccountId = :savingsAccountId")
    suspend fun latestId(savingsAccountId: Int): Int?

    @Query("SELECT * FROM TransactionTable WHERE savingsAccountId = :savingsAccountId ORDER BY id DESC")
    fun pageFlow(savingsAccountId: Int): Flow<List<SavingsAccountTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnoreDuplicates(rows: List<SavingsAccountTransactionEntity>)

    @Transaction
    suspend fun appendNewRows(savingsAccountId: Int, rows: List<SavingsAccountTransactionEntity>) {
        // S5-PAGE-ATOMIC: insert-only-new (IGNORE conflict on existing id), never
        // wipe+reinsert the whole page. Atomic so a concurrent read never observes a
        // half-written ledger.
        insertAllIgnoreDuplicates(rows)
    }

    @Query("DELETE FROM TransactionTable WHERE savingsAccountId = :savingsAccountId")
    suspend fun deletePage(savingsAccountId: Int)

    @Query("DELETE FROM TransactionTable")
    suspend fun deleteAll()
}
