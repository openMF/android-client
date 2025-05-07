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
import com.mifos.core.data.repository.LoanAccountDisbursementRepository
import com.mifos.core.model.objects.account.loan.LoanDisbursement
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanAccountDisbursementRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
) : LoanAccountDisbursementRepository {

    override fun getLoanTransactionTemplate(
        loanId: Int,
        command: String?,
    ): Flow<DataState<LoanTransactionTemplate>> {
        return dataManagerLoan.getLoanTransactionTemplate(loanId, command)
            .asDataStateFlow()
    }

    override fun disburseLoan(
        loanId: Int,
        loanDisbursement: LoanDisbursement?,
    ): Flow<DataState<GenericResponse>> {
        return dataManagerLoan.disburseLoan(loanId, loanDisbursement)
            .asDataStateFlow()
    }
}
