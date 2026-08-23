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
import com.mifos.room.entities.accounts.loans.LoanTransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the loan-transaction ledger ([LoanTransactionEntity], table `loan_transactions`).
 *
 * The write path is deliberately **insert-only-new** (S5-PAGE-ATOMIC): the network
 * always returns the FULL nested transaction list (Fineract has no since-keyed delta
 * endpoint — see `LoanTransactionStore`), but [appendNewRows] persists it inside a
 * single `@Transaction` using conflict-IGNORE so existing rows are never wiped and
 * re-inserted. [latestId] exposes the cached watermark for callers that want to reason
 * about the highest already-persisted transaction id.
 */
@Dao
interface LoanTransactionDao {

    @Query("SELECT MAX(id) FROM loan_transactions WHERE loanId = :loanId")
    suspend fun latestId(loanId: Int): Long?

    @Query("SELECT * FROM loan_transactions WHERE loanId = :loanId ORDER BY id DESC")
    fun pageFlow(loanId: Int): Flow<List<LoanTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnoreDuplicates(rows: List<LoanTransactionEntity>)

    @Transaction
    suspend fun appendNewRows(loanId: Int, rows: List<LoanTransactionEntity>) {
        // S5-PAGE-ATOMIC: insert-only-new (IGNORE conflict on existing id), never
        // wipe+reinsert the whole page. Atomic so a concurrent read never observes a
        // half-written ledger.
        insertAllIgnoreDuplicates(rows)
    }

    @Query("DELETE FROM loan_transactions WHERE loanId = :loanId")
    suspend fun deletePage(loanId: Int)

    @Query("DELETE FROM loan_transactions")
    suspend fun deleteAll()
}
