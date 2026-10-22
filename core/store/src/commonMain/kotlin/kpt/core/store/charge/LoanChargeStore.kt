/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.store.charge

import kpt.core.base.store.annotation.CacheKey
import kpt.core.base.store.annotation.StoreProvider
import kpt.core.base.store.infra.StoreFactory
import kpt.core.database.charge.dao.ChargeDao
import kpt.core.database.charge.entity.ChargesEntity
import kpt.core.network.mifos.loan.api.LoanApi
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

/**
 * Loan charges — READ-CACHE (`createStore`), keyed by loanId.
 *
 * The sibling this file was predicted by: [provideClientChargeStore]'s own note says "a loan-charge
 * store would be its own provider with its own key", and this is it. Keyed for the same reason — a
 * `Unit` key would collapse every loan's charges into one slot and serve the previously-viewed
 * loan's rows (the Store5 `inconsistent-read-paths` anti-pattern).
 *
 * Separate from the client store rather than one store with a resource-type key: the two read
 * DIFFERENT endpoints (`loans/{id}/charges` returns a bare list, `clients/{id}/charges` returns a
 * `Page`) and different Room columns, so sharing a store would mean branching inside both the
 * fetcher and the source of truth on a key the cache cannot distinguish.
 */
@StoreProvider(id = "loanCharges", ttl = "15m")
@CacheKey(fn = "forLoan", key = "loanCharges:{loanId}", params = ["loanId:Int"])
fun provideLoanChargeStore(
    service: LoanApi,
    dao: ChargeDao,
): Store<Int, List<ChargesEntity>> = StoreFactory.createStore(
    fetcher = Fetcher.of { loanId: Int -> service.getListOfLoanCharges(loanId) },
    sourceOfTruth = SourceOfTruth.of(
        reader = { loanId: Int -> dao.getLoanCharges(loanId) },
        // Stamps the owning loan, because `loans/{id}/charges` does not echo `loanId` back on each
        // row — without it every charge would land with a null `loanId` and the reader would
        // return nothing on the next cold start.
        writer = { loanId: Int, charges: List<ChargesEntity> ->
            dao.insertAllCharges(charges.map { it.copy(loanId = loanId) })
        },
    ),
)
