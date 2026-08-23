/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp.loan

import com.mifos.core.data.repository.loan.LoanTransactionsRepository
import com.mifos.core.model.objects.account.loan.Transaction
import com.mifos.core.model.objects.account.loan.Type
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import com.mifos.core.model.objects.account.loan.loanWithAssociations.SavingAccountCurrency
import com.mifos.room.entities.accounts.loans.LoanTransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.datetime.LocalDate
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

/**
 * Created by Aditya Gupta on 12/08/23.
 *
 * Offline-first read path (offline-first-template-migration, pilot 04 / sub-plan 20). The loan
 * transaction ledger is served through the Store5 [LoanTransactionStore][com.mifos.core.store.provideLoanTransactionStore]
 * (qualifier [AppStoreRegistry.LoanTransactions][kpt.core.store.AppStoreRegistry.LoanTransactions])
 * instead of a raw `DataManager.getLoanTransactions` network call. `StoreReadRequest.cached(refresh
 * = true)` emits the Room-persisted rows immediately (so the ledger renders offline from cache) AND
 * triggers a background network refresh when connectivity is available (SWR). The fetcher's network
 * error is intentionally swallowed — Store5's source-of-truth reader still emits the cached (possibly
 * empty) ledger, so the screen shows cached rows offline rather than a "Unable to resolve host"
 * error (the class of defect device-truth surfaced on 2026-08-23 when the store existed but no
 * consumer streamed from it).
 */
class LoanTransactionsRepositoryImp(
    private val loanTransactionStore: Store<Int, List<LoanTransactionEntity>>,
) : LoanTransactionsRepository {

    override fun getLoanTransactions(loan: Int): Flow<LoanWithAssociations> {
        return loanTransactionStore
            .stream(StoreReadRequest.cached(key = loan, refresh = true))
            .mapNotNull { response ->
                when (response) {
                    is StoreReadResponse.Data -> response.value.toLoanWithAssociations()
                    // Offline-first: the fetch error is non-fatal. The SoT reader emits the cached
                    // ledger (empty when nothing was ever cached) as a separate Data response, so we
                    // never surface a network error here — the "No internet connection" banner already
                    // signals offline state to the officer.
                    else -> null
                }
            }
    }
}

// ---------------------------------------------------------------------------
// Inline mapping helpers — private to this file
// ---------------------------------------------------------------------------

/**
 * Projects the cached [LoanTransactionEntity] rows back into the [LoanWithAssociations] shape the
 * `loanTransaction` screen consumes (it reads `transactions` + `currency`). Loan-level fields the
 * ledger screen does not render are left at their defaults — the cache stores only the transaction
 * rows (see `provideLoanTransactionStore`).
 */
private fun List<LoanTransactionEntity>.toLoanWithAssociations(): LoanWithAssociations =
    LoanWithAssociations(
        transactions = map { it.toTransaction() },
        currency = firstOrNull()?.currencyCode?.let { SavingAccountCurrency(code = it) },
    )

private fun LoanTransactionEntity.toTransaction(): Transaction = Transaction(
    id = id.toInt(),
    officeName = officeName,
    type = typeValue?.let { Type(value = it) },
    date = dateEpochDay.toDateList(),
    amount = amount,
    manuallyReversed = manuallyReversed,
)

/** Reverse of the store's `LocalDate(...).toEpochDays()` encoding — back to Fineract's `[y, m, d]`. */
private fun Long?.toDateList(): List<Int> {
    if (this == null) return emptyList()
    val d = LocalDate.fromEpochDays(toInt())
    return listOf(d.year, d.monthNumber, d.dayOfMonth)
}
