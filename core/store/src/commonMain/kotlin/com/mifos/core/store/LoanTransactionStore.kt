/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.store

import com.mifos.core.network.DataManager
import com.mifos.core.network.dto.loan.LoanTransactionDto
import com.mifos.room.dao.LoanTransactionDao
import com.mifos.room.entities.accounts.loans.LoanTransactionEntity
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kpt.core.base.store.infra.StoreFactory
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import kotlin.time.Duration

/**
 * Build the read-only offline-first [Store] for a loan's transaction ledger, keyed by
 * `loanId` and streaming the persisted [LoanTransactionEntity] rows.
 *
 * ### Honest "delta" semantics — NO network-level delta exists
 * Fineract exposes **no** incremental / since-keyed transactions-list endpoint. The only
 * surface is `GET .../{loanId}?associations=transactions`, a FULL loan-details fetch with
 * the transactions nested inside ([DataManager.getLoanTransactions] →
 * `LoanWithAssociationsDto.transactions`). So this Store CANNOT reduce the network payload —
 * the fetcher always pulls the full nested list. The "incremental append" is purely a
 * CLIENT-SIDE Room semantic: [LoanTransactionDao.appendNewRows] inserts inside one
 * `@Transaction` with conflict-IGNORE, so already-cached rows are never wiped and
 * re-inserted (S5-PAGE-ATOMIC), and the cached MAX(id) watermark
 * ([LoanTransactionDao.latestId]) reflects the highest persisted transaction. This is a
 * genuine incremental-append at the persistence layer, not a network delta.
 *
 * ### Fetcher shape
 * [DataManager.getLoanTransactions] returns a cold `Flow<LoanWithAssociationsDto>`, so
 * [Fetcher.ofFlow] is used (rather than a `.first()`-bridged [Fetcher.of]) — it composes
 * the DataManager Flow straight through with a `map` that projects to
 * `List<LoanTransactionEntity>` without an extra suspension bridge, matching how
 * `StoreFactory` itself constructs Flow-backed fetchers.
 *
 * @param api the network access seam ([DataManager]) exposing the full-fetch call.
 * @param dao the Room DAO backing the local ledger source-of-truth.
 * @return a [Store] whose key is the loan id and whose value is the cached ledger.
 */
fun provideLoanTransactionStore(
    api: DataManager,
    dao: LoanTransactionDao,
): Store<Int, List<LoanTransactionEntity>> {
    return StoreFactory.createStore(
        fetcher = Fetcher.ofFlow { loanId: Int ->
            api.getLoanTransactions(loanId).map { dto ->
                dto.transactions.orEmpty().map { it.toEntity(loanId) }
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { loanId: Int -> dao.pageFlow(loanId) },
            writer = { loanId: Int, rows: List<LoanTransactionEntity> ->
                dao.appendNewRows(loanId, rows)
            },
            delete = { loanId: Int -> dao.deletePage(loanId) },
            deleteAll = { dao.deleteAll() },
        ),
        // Immutable ledger rows — once written they never expire in-memory.
        memoryPolicy = MemoryPolicy.builder<Int, List<LoanTransactionEntity>>()
            .setExpireAfterWrite(Duration.INFINITE)
            .build(),
    )
}

// ---------------------------------------------------------------------------
// Inline mapping helpers — private to this file
// ---------------------------------------------------------------------------

private fun LoanTransactionDto.toEntity(loanId: Int): LoanTransactionEntity = LoanTransactionEntity(
    id = id,
    loanId = loanId,
    officeName = officeName,
    typeValue = type?.value,
    amount = amount,
    dateEpochDay = date.toEpochDayOrNull(),
    currencyCode = currency?.code,
    manuallyReversed = manuallyReversed,
)

/** Fineract encodes dates as `[year, month, day]`; flatten to an epoch-day scalar. */
private fun List<Int>?.toEpochDayOrNull(): Long? {
    if (this == null || size < 3) return null
    return runCatching { LocalDate(this[0], this[1], this[2]).toEpochDays().toLong() }.getOrNull()
}
