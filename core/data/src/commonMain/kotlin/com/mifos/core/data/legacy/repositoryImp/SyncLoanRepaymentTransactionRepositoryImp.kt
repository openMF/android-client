/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.legacy.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.SyncLoanRepaymentTransactionRepository
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.loan.entity.LoanRepaymentRequestEntity
import com.mifos.room.loan.entity.LoanRepaymentResponseEntity
import com.mifos.room.savings.entity.PaymentTypeOptionEntity
import kotlinx.coroutines.flow.Flow

class SyncLoanRepaymentTransactionRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
) : SyncLoanRepaymentTransactionRepository {

    override fun databaseLoanRepayments(): Flow<DataState<List<LoanRepaymentRequestEntity>>> {
        return dataManagerLoan.databaseLoanRepayments
            .asDataStateFlow()
    }

    override fun paymentTypeOption(): Flow<DataState<List<PaymentTypeOptionEntity>>> {
        return dataManagerLoan.paymentTypeOption
            .asDataStateFlow()
    }

    override suspend fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequestEntity,
    ): LoanRepaymentResponseEntity {
        return dataManagerLoan.submitPayment(loanId, request)
    }

    override fun deleteAndUpdateLoanRepayments(loanId: Int): Flow<DataState<List<LoanRepaymentRequestEntity>>> {
        return dataManagerLoan.deleteAndUpdateLoanRepayments(loanId)
            .asDataStateFlow()
    }

    override fun updateLoanRepaymentTransaction(loanRepaymentRequest: LoanRepaymentRequestEntity): Flow<DataState<LoanRepaymentRequestEntity>> {
        return dataManagerLoan.updateLoanRepaymentTransaction(loanRepaymentRequest)
            .asDataStateFlow()
    }
}
