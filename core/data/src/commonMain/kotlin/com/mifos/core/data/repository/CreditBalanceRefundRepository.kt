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
import com.mifos.core.model.objects.account.loan.LoanRefundDetails
import com.mifos.room.entities.accounts.loans.CreditBalanceRefundRequest
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Credit Balance Refund operations.
 * Provides methods to load loan details and submit refund transactions.
 */
interface CreditBalanceRefundRepository {

    val updateTrigger: Flow<Unit>

    /**
     * Fetches loan details (client name, account number, overpaid amount, currency)
     * needed to populate the refund form.
     *
     * @param loanId The ID of the loan account
     * @return Flow of DataState wrapping the loan details
     */
    fun getLoanById(loanId: Int): Flow<DataState<LoanRefundDetails?>>

    /**
     * Submits a credit balance refund transaction for a loan account.
     * Error handling (HTTP exceptions, network errors) is done inside the repository.
     *
     * @param loanId The ID of the loan account
     * @param request The refund request containing transaction details
     * @return DataState wrapping the response or an error
     */
    suspend fun submitRefund(
        loanId: Int,
        request: CreditBalanceRefundRequest,
    ): DataState<LoanRepaymentResponseEntity>
}
