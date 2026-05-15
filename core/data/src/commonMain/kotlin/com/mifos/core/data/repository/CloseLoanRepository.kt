/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.account.loan.CloseLoanRequest
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Repository abstraction for the close-loan transaction flow.
 */
interface CloseLoanRepository {

    /**
     * Fetches the close-loan template (`command=close`) for a specific loan.
     *
     * @param loanId The unique identifier of the loan account.
     * @return A [Flow] emitting the [DataState] of the [LoanTransactionTemplate].
     */
    fun getCloseLoanTemplate(loanId: Int): Flow<DataState<LoanTransactionTemplate?>>

    /**
     * Submits a close transaction for a loan account.
     *
     * @param loanId The unique identifier of the loan account to close.
     * @param request The [CloseLoanRequest] payload.
     * @return [DataState.Success] on completion, [DataState.Error] on failure or offline.
     */
    suspend fun closeLoanAccount(loanId: Int, request: CloseLoanRequest): DataState<Unit>

    /**
     * Re-fetches the loan account from the server to ensure local state is synchronized.
     *
     * @param loanId The unique identifier of the loan account to synchronize.
     * @return [DataState.Success] on completion, [DataState.Error] on failure or offline.
     */
    suspend fun syncLoanAccount(loanId: Int): DataState<Unit>
}
