/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.room.entities.PaymentTypeOption
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequest
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponse
import kotlinx.coroutines.flow.Flow

interface SyncLoanRepaymentTransactionRepository {

    fun databaseLoanRepayments(): Flow<List<LoanRepaymentRequest>>

    fun paymentTypeOption(): Flow<List<PaymentTypeOption>>

    suspend fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest,
    ): LoanRepaymentResponse

    fun deleteAndUpdateLoanRepayments(loanId: Int): Flow<List<LoanRepaymentRequest>>

    fun updateLoanRepaymentTransaction(
        loanRepaymentRequest: LoanRepaymentRequest,
    ): Flow<LoanRepaymentRequest?>
}
