/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.CreditBalanceRefundRepository
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.accounts.loans.CreditBalanceRefundRequest
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity

/**
 * Implementation of CreditBalanceRefundRepository.
 * Delegates credit balance refund operations to DataManagerLoan.
 */
class CreditBalanceRefundRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
) : CreditBalanceRefundRepository {

    override suspend fun submitRefund(
        loanId: Int,
        request: CreditBalanceRefundRequest,
    ): LoanRepaymentResponseEntity {
        return dataManagerLoan.submitCreditBalanceRefund(loanId, request)
    }
}
