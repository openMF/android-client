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

import com.mifos.room.entities.accounts.loans.LoanRepaymentRequest
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponse
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface LoanRepaymentRepository {

    fun getLoanRepayTemplate(loanId: Int): Flow<LoanRepaymentTemplate?>

    suspend fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest,
    ): LoanRepaymentResponse

    fun getDatabaseLoanRepaymentByLoanId(loanId: Int): Flow<LoanRepaymentRequest?>
}
