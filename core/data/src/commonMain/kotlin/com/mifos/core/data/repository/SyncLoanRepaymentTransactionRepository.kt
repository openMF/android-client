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

import com.mifos.core.common.utils.DataState
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import kotlinx.coroutines.flow.Flow

interface SyncLoanRepaymentTransactionRepository {

    fun databaseLoanRepayments(): Flow<DataState<List<LoanRepaymentRequestEntity>>>

    fun paymentTypeOption(): Flow<DataState<List<PaymentTypeOptionEntity>>>

    suspend fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequestEntity,
    ): LoanRepaymentResponseEntity

    fun deleteAndUpdateLoanRepayments(
        loanId: Int,
    ): Flow<DataState<List<LoanRepaymentRequestEntity>>>

    fun updateLoanRepaymentTransaction(
        loanRepaymentRequest: LoanRepaymentRequestEntity,
    ): Flow<DataState<LoanRepaymentRequestEntity?>>
}
