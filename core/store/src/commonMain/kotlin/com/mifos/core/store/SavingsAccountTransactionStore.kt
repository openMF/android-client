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

import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.room.dao.SavingsAccountTransactionDao
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionEntity
import kotlinx.coroutines.flow.map
import kpt.core.base.store.infra.StoreFactory
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import kotlin.time.Duration

/**
 * Build the read-only offline-first [Store] for a savings account's transaction ledger,
 * keyed by `savingsAccountId` and streaming the persisted [SavingsAccountTransactionEntity]
 * rows (the EXISTING `TransactionTable` — no duplicate entity is introduced).
 *
 * ### Honest "delta" semantics — NO network-level delta exists
 * As with the loan ledger, Fineract exposes **no** since-keyed savings-transactions
 * endpoint. The only surface is `GET {savingsaccounts}/{id}?associations=transactions`, a
 * FULL savings-account-details fetch with transactions nested inside
 * ([DataManagerSavings.getSavingsAccount] → `SavingsAccountWithAssociationsEntity.transactions`).
 * The fetcher always pulls the full nested list; the incremental-append is a CLIENT-SIDE
 * Room semantic only ([SavingsAccountTransactionDao.appendNewRows] — single `@Transaction`,
 * conflict-IGNORE, never wipe+reinsert; S5-PAGE-ATOMIC).
 *
 * ### Real data source (plan correction)
 * The transaction-history rows are read from the same `?associations=transactions`
 * full-fetch that backs `SavingsAccountSummaryScreen` — i.e. the nested `transactions`
 * on `SavingsAccountWithAssociationsEntity`, NOT the deposit/withdrawal submission form's
 * repository. `type = "savingsaccounts"` is the standard Fineract savings-account path
 * segment; the account-type nuance carried by the summary ViewModel is out of scope for
 * this store-layer wiring (a follow-up task owns the ViewModel/Screen cutover).
 *
 * ### Fetcher shape
 * [DataManagerSavings.getSavingsAccount] returns a cold `Flow<SavingsAccountWithAssociationsEntity?>`,
 * so [Fetcher.ofFlow] composes it straight through with a `map` that projects to
 * `List<SavingsAccountTransactionEntity>` (stamping the scoping `savingsAccountId` onto
 * each nested row) — no `.first()` bridge needed.
 *
 * @param api the savings network access seam ([DataManagerSavings]).
 * @param dao the Room DAO backing the local ledger source-of-truth.
 * @return a [Store] whose key is the savings-account id and whose value is the cached ledger.
 */
fun provideSavingsAccountTransactionStore(
    api: DataManagerSavings,
    dao: SavingsAccountTransactionDao,
): Store<Int, List<SavingsAccountTransactionEntity>> {
    return StoreFactory.createStore(
        fetcher = Fetcher.ofFlow { savingsAccountId: Int ->
            api.getSavingsAccount(SAVINGS_ACCOUNT_TYPE, savingsAccountId, ASSOCIATION_TRANSACTIONS)
                .map { association ->
                    association?.transactions.orEmpty()
                        .map { it.copy(savingsAccountId = savingsAccountId) }
                }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { savingsAccountId: Int -> dao.pageFlow(savingsAccountId) },
            writer = { savingsAccountId: Int, rows: List<SavingsAccountTransactionEntity> ->
                dao.appendNewRows(savingsAccountId, rows)
            },
            delete = { savingsAccountId: Int -> dao.deletePage(savingsAccountId) },
            deleteAll = { dao.deleteAll() },
        ),
        // Immutable ledger rows — once written they never expire in-memory.
        memoryPolicy = MemoryPolicy.builder<Int, List<SavingsAccountTransactionEntity>>()
            .setExpireAfterWrite(Duration.INFINITE)
            .build(),
    )
}

private const val SAVINGS_ACCOUNT_TYPE = "savingsaccounts"
private const val ASSOCIATION_TRANSACTIONS = "transactions"
