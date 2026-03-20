/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.room.entities.accounts.loans.CreditBalanceRefundRequest
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity

/**
 * Repository interface for Credit Balance Refund operations.
 * Provides methods to submit refund transactions for overpaid loan accounts.
 */
interface CreditBalanceRefundRepository {

    /**
     * Submits a credit balance refund transaction for a loan account.
     *
     * @param loanId The ID of the loan account
     * @param request The refund request containing transaction details
     * @return LoanRepaymentResponseEntity containing the transaction result
     */
    suspend fun submitRefund(
        loanId: Int,
        request: CreditBalanceRefundRequest,
    ): LoanRepaymentResponseEntity
}
