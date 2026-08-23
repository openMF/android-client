/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

/**
 * Created by Aditya Gupta on 08/08/23.
 *
 * Offline-first read path: [getSavingsAccount] streams the Store5 savings-summary store
 * (qualifier [AppStoreRegistry.SavingsAccountSummary][kpt.core.store.AppStoreRegistry.SavingsAccountSummary] /
 * [provideSavingsAccountSummaryStore][com.mifos.core.store.provideSavingsAccountSummaryStore])
 * instead of the raw `DataManagerSavings.getSavingsAccount` network call.
 * `StoreReadRequest.cached(refresh = true)` emits the Room-persisted
 * [SavingsAccountWithAssociationsEntity] immediately (renders offline from cache) AND triggers a
 * background network refresh (SWR). The fetcher's network error is intentionally swallowed — the
 * source-of-truth reader still emits the cached row, so the screen shows cached data offline
 * rather than an "Unable to resolve host" error.
 *
 * NOTE: `type` and `association` are honored by the store's hardcoded path segments
 * (`savingsaccounts` / `transactions`); the store keys purely on `savingsAccountId` (mirrors the
 * sibling `provideSavingsAccountTransactionStore`).
 */
class SavingsAccountSummaryRepositoryImp(
    private val savingsSummaryStore: Store<Int, SavingsAccountWithAssociationsEntity>,
) : SavingsAccountSummaryRepository {

    override fun getSavingsAccount(
        type: String,
        savingsAccountId: Int,
        association: String?,
    ): Flow<SavingsAccountWithAssociationsEntity?> {
        return savingsSummaryStore
            .stream(StoreReadRequest.cached(key = savingsAccountId, refresh = true))
            .mapNotNull { response ->
                when (response) {
                    is StoreReadResponse.Data -> response.value
                    // Offline-first: fetch error is non-fatal. The SoT reader emits the cached
                    // account as a separate Data response, so we never surface a network error.
                    else -> null
                }
            }
    }
}
