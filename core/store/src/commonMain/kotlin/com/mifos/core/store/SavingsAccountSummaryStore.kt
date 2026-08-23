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
import com.mifos.room.dao.SavingsDao
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import kotlinx.coroutines.flow.map
import kpt.core.base.store.infra.StoreFactory
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import kotlin.time.Duration.Companion.minutes

/**
 * Build the read-only offline-first [Store] for a single savings account's SUMMARY, keyed by
 * `savingsAccountId` and streaming the FULL persisted [SavingsAccountWithAssociationsEntity] row
 * (header fields + nested transactions) that `SavingsAccountSummaryScreen` renders.
 *
 * ### Read path (mirror of [provideGroupStore])
 * The summary screen must render from the local cache when the officer is offline. This Store
 * fronts [DataManagerSavings.getSavingsAccount] (the SAME full `?associations=transactions`
 * fetch the previous network-first
 * [SavingsAccountSummaryRepositoryImp][com.mifos.core.data.repositoryImp.SavingsAccountSummaryRepositoryImp]
 * used) with a Room source-of-truth so `StoreReadRequest.cached(refresh = true)` emits the
 * persisted entity immediately AND kicks a background refresh (stale-while-revalidate). The
 * fetch error is intentionally non-fatal at the consumer — the SoT reader still emits the
 * cached row so the screen shows cached data offline rather than an "Unable to resolve host"
 * error.
 *
 * ### Fetcher shape — full entity, NOT transactions-only
 * Unlike [provideSavingsAccountTransactionStore] (which projects to the nested `transactions`
 * list), this store caches the WHOLE [SavingsAccountWithAssociationsEntity]. [DataManagerSavings.getSavingsAccount]
 * returns a cold `Flow<SavingsAccountWithAssociationsEntity?>`, so [Fetcher.ofFlow] composes it
 * straight through with a `map` that (a) rejects a null network body (throwing keeps the fetch
 * error non-fatal — the SoT still serves cache) and (b) STAMPS the business key onto the row's
 * `id` so the SoT reader's `WHERE id = :savingsAccountId` predicate always matches.
 *
 * ### type / association are HARDCODED (matches the sibling transactions store)
 * `type = "savingsaccounts"` is the standard Fineract savings-account path segment and
 * `association = "transactions"` is the fixed [Constants.TRANSACTIONS][com.mifos.core.common.utils.Constants.TRANSACTIONS]
 * value the summary ViewModel passes. The account-type nuance (fixed / recurring deposit
 * endpoints) carried by the summary ViewModel is out of scope for this store-layer wiring — the
 * same limitation the existing [provideSavingsAccountTransactionStore] already accepts (the
 * `Store<Int, ...>` key space carries no `type`).
 *
 * ### Source of truth — autoGenerate-PK dedupe on refresh
 * The [SavingsAccountWithAssociationsEntity] `@PrimaryKey(autoGenerate = true) id` is the
 * business key (there is NO separate `savingsAccountId` column — the existing DAO reader keys on
 * `WHERE id = :savingsAccountId`). A plain insert on every SWR refresh could duplicate rows for
 * the same account, so the writer DELETEs-by-id then inserts the key-stamped row (replace
 * semantics per business key). reader = [SavingsDao.getSavingsAccountWithAssociations]
 * (`Flow<Entity?>` — null = empty cache). writer = delete-then-insert. delete / deleteAll =
 * [SavingsDao.deleteSavingsAccountWithAssociations] / [SavingsDao.deleteAllSavingsAccountWithAssociations]
 * so `Store.clear()` wipes the SavingsAccountWithAssociations table on logout via
 * `StoreCacheManager` (D7).
 *
 * @param api the savings network access seam ([DataManagerSavings]) exposing the full-account fetch.
 * @param dao the Room DAO backing the local savings-account source-of-truth.
 * @return a [Store] whose key is the savings-account id and whose value is the cached full account.
 */
fun provideSavingsAccountSummaryStore(
    api: DataManagerSavings,
    dao: SavingsDao,
): Store<Int, SavingsAccountWithAssociationsEntity> {
    return StoreFactory.createStore(
        fetcher = Fetcher.ofFlow { savingsAccountId: Int ->
            api.getSavingsAccount(SAVINGS_ACCOUNT_TYPE, savingsAccountId, ASSOCIATION_TRANSACTIONS)
                .map { account ->
                    // Reject a null body so the fetch failure is non-fatal (SoT serves cache);
                    // stamp the business key onto `id` so the reader's WHERE id = :key matches.
                    (account ?: throw NoSuchElementException("No savings account $savingsAccountId"))
                        .copy(id = savingsAccountId)
                }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { savingsAccountId: Int -> dao.getSavingsAccountWithAssociations(savingsAccountId) },
            writer = { savingsAccountId: Int, account: SavingsAccountWithAssociationsEntity ->
                // autoGenerate PK: delete the prior row for this business key, then insert the
                // key-stamped row so refresh REPLACES (never duplicates) per savingsAccountId.
                dao.deleteSavingsAccountWithAssociations(savingsAccountId)
                dao.insertSavingsAccountWithAssociations(account.copy(id = savingsAccountId))
            },
            delete = { savingsAccountId: Int -> dao.deleteSavingsAccountWithAssociations(savingsAccountId) },
            deleteAll = { dao.deleteAllSavingsAccountWithAssociations() },
        ),
        // A savings account is mutable (balance, status, transactions change) — expire the
        // in-memory copy after a short window so a revisit re-reads Room / refreshes network.
        memoryPolicy = MemoryPolicy.builder<Int, SavingsAccountWithAssociationsEntity>()
            .setExpireAfterWrite(5.minutes)
            .build(),
    )
}

private const val SAVINGS_ACCOUNT_TYPE = "savingsaccounts"
private const val ASSOCIATION_TRANSACTIONS = "transactions"
