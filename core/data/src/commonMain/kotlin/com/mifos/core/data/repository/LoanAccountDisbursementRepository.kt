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
import com.mifos.core.model.objects.account.loan.LoanDisbursement
import com.mifos.core.network.GenericResponse
import com.mifos.room.entities.templates.loans.LoanTransactionTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface LoanAccountDisbursementRepository {

    fun getLoanTransactionTemplate(
        loanId: Int,
        command: String?,
    ): Flow<DataState<LoanTransactionTemplate>>

    fun disburseLoan(
        loanId: Int,
        loanDisbursement: LoanDisbursement?,
    ): Flow<DataState<GenericResponse>>
}
