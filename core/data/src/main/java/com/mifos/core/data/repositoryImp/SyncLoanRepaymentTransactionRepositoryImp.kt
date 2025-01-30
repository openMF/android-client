/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.SyncLoanRepaymentTransactionRepository
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.PaymentTypeOption
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequest
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncLoanRepaymentTransactionRepositoryImp @Inject constructor(
    private val dataManagerLoan: DataManagerLoan,
) : SyncLoanRepaymentTransactionRepository {

    override fun databaseLoanRepayments(): Flow<List<LoanRepaymentRequest>> {
        return dataManagerLoan.databaseLoanRepayments
    }

    override fun paymentTypeOption(): Flow<List<PaymentTypeOption>> {
        return dataManagerLoan.paymentTypeOption
    }

    override suspend fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest,
    ): LoanRepaymentResponse {
        return dataManagerLoan.submitPayment(loanId, request)
    }

    override fun deleteAndUpdateLoanRepayments(loanId: Int): Flow<List<LoanRepaymentRequest>> {
        return dataManagerLoan.deleteAndUpdateLoanRepayments(loanId)
    }

    override fun updateLoanRepaymentTransaction(loanRepaymentRequest: LoanRepaymentRequest): Flow<LoanRepaymentRequest> {
        return dataManagerLoan.updateLoanRepaymentTransaction(loanRepaymentRequest)
    }
}
