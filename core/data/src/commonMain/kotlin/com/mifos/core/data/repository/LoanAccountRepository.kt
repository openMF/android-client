/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.account.loan.RepaymentSchedule
import com.mifos.core.model.objects.organisations.LoanProducts
import com.mifos.core.network.model.LoansPayload
import com.mifos.room.entities.templates.loans.LoanTemplate
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface LoanAccountRepository {

    fun allLoans(): Flow<DataState<List<LoanProducts>>>

    fun getLoansAccountTemplate(clientId: Int, productId: Int): Flow<DataState<LoanTemplate>>

    fun createLoansAccount(loansPayload: LoansPayload): Flow<DataState<HttpResponse>>

    /**
     * Calculate loan repayment schedule without creating the loan.
     * Used to preview the schedule before submitting the loan application.
     */
    fun calculateLoanSchedule(loansPayload: LoansPayload): Flow<DataState<RepaymentSchedule>>
}
