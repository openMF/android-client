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

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mifos.room.MifosDatabase
import com.mifos.room.entities.accounts.loans.LoanTransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Device-free proof of the offline-first loan-transaction ledger contract that the pilot
 * (offline-first-template-migration sub-plan 04 T4/T5) proves on-device — exercised here
 * against real Room (bundled SQLite) so the store's persistence semantics are locked without
 * a live backend:
 *
 *  - **Offline full-render:** [LoanTransactionDao.pageFlow] emits the FULL persisted ledger
 *    (newest id first), so a cold offline launch renders every cached row — the same read the
 *    Store5 `SourceOfTruth` streams when the network is unreachable.
 *  - **Reconnect appends ONLY new rows (S5-PAGE-ATOMIC):** the Fineract fetch always returns the
 *    full nested transaction list, so [LoanTransactionDao.appendNewRows] re-delivers overlapping
 *    ids; the insert-IGNORE keeps existing rows untouched and adds only the genuinely-new ids,
 *    never wiping + re-inserting the page.
 *  - **Watermark:** [LoanTransactionDao.latestId] tracks MAX(id) across appends.
 *  - Pages are isolated by loanId.
 */
class LoanTransactionDaoTest {

    private lateinit var db: MifosDatabase
    private lateinit var dao: LoanTransactionDao

    @BeforeTest
    fun setup() {
        db = Room.inMemoryDatabaseBuilder<MifosDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        dao = db.loanTransactionDao
    }

    @AfterTest
    fun teardown() {
        db.close()
    }

    private fun tx(id: Long, loan: Int = 1) =
        LoanTransactionEntity(id = id, loanId = loan, amount = 100.0)

    @Test
    fun offlineRenderReflectsFullCachedLedger() = runTest {
        dao.appendNewRows(1, listOf(tx(1), tx(2), tx(3)))

        val page = dao.pageFlow(1).first()

        // Newest-first: the offline cold-launch renders the whole cached ledger.
        assertEquals(listOf(3L, 2L, 1L), page.map { it.id })
        assertEquals(3L, dao.latestId(1))
    }

    @Test
    fun reconnectAppendsOnlyNewRows() = runTest {
        dao.appendNewRows(1, listOf(tx(1), tx(2), tx(3)))

        // Reconnect: the full-list fetch re-delivers overlaps (2, 3) plus new rows (4, 5).
        dao.appendNewRows(1, listOf(tx(2), tx(3), tx(4), tx(5)))

        val ids = dao.pageFlow(1).first().map { it.id }
        assertEquals(listOf(5L, 4L, 3L, 2L, 1L), ids) // only 4 & 5 appended; 2 & 3 ignored
        assertEquals(5L, dao.latestId(1))
    }

    @Test
    fun pagesIsolatedByLoanId() = runTest {
        dao.appendNewRows(1, listOf(tx(id = 1, loan = 1)))
        dao.appendNewRows(2, listOf(tx(id = 9, loan = 2)))

        assertEquals(listOf(1L), dao.pageFlow(1).first().map { it.id })
        assertEquals(listOf(9L), dao.pageFlow(2).first().map { it.id })
    }
}
