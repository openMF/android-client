/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.CreditBalanceRefundRepository
import com.mifos.core.model.objects.account.loan.creditBalanceRefund.CreditBalanceRefundInput
import com.mifos.core.model.objects.account.loan.creditBalanceRefund.CreditBalanceRefundResponse
import com.mifos.core.model.objects.account.loan.creditBalanceRefund.LoanRefundDetails

/**
 * Use case for credit balance refund operations.
 * Provides methods to fetch loan details and submit refund transactions.
 */
class CreditBalanceRefundUseCase(
    private val repository: CreditBalanceRefundRepository,
) {
    /**
     * Fetches loan details needed for the refund form.
     *
     * @param loanId The ID of the loan account
     * @return Flow of DataState wrapping the loan refund details
     */
    suspend fun getLoanRefundDetails(loanId: Int): DataState<LoanRefundDetails?> {
        return repository.getLoanById(loanId)
    }

    /**
     * Submits a credit balance refund transaction.
     *
     * @param loanId The ID of the loan account
     * @param input The refund input containing transaction details
     * @return DataState wrapping Unit on success or error
     */
    suspend fun submitRefund(loanId: Int, input: CreditBalanceRefundInput): DataState<CreditBalanceRefundResponse> {
        return repository.submitRefund(loanId, input)
    }
}
