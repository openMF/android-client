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

import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequest
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponse
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanRepaymentRepositoryImp @Inject constructor(
    private val dataManagerLoan: DataManagerLoan,
) : LoanRepaymentRepository {

    override fun getLoanRepayTemplate(loanId: Int): Flow<LoanRepaymentTemplate?> {
        return dataManagerLoan.getLoanRepayTemplate(loanId)
    }

    override suspend fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest,
    ): LoanRepaymentResponse {
        return dataManagerLoan.submitPayment(loanId, request)
    }

    override fun getDatabaseLoanRepaymentByLoanId(loanId: Int): Flow<LoanRepaymentRequest?> {
        return dataManagerLoan.getDatabaseLoanRepaymentByLoanId(loanId)
    }
}
