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

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanRepaymentRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
) : LoanRepaymentRepository {

    override fun getLoanRepayTemplate(loanId: Int): Flow<DataState<LoanRepaymentTemplateEntity?>> {
        return dataManagerLoan.getLoanRepayTemplate(loanId)
            .asDataStateFlow()
    }

    override suspend fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequestEntity,
    ): LoanRepaymentResponseEntity {
        return dataManagerLoan.submitPayment(loanId, request)
    }

    override fun getDatabaseLoanRepaymentByLoanId(loanId: Int): Flow<DataState<LoanRepaymentRequestEntity?>> {
        return dataManagerLoan.getDatabaseLoanRepaymentByLoanId(loanId)
            .asDataStateFlow()
    }
}
