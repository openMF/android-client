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
import com.mifos.core.model.objects.organisations.LoanProducts
import com.mifos.core.network.model.LoansPayload
import com.mifos.room.entities.accounts.loans.Loan
import com.mifos.room.entities.templates.loans.LoanTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface LoanAccountRepository {

    fun allLoans(): Flow<DataState<List<LoanProducts>>>

    fun getLoansAccountTemplate(clientId: Int, productId: Int): Flow<DataState<LoanTemplate>>

    fun createLoansAccount(loansPayload: LoansPayload): Flow<DataState<Loan>>
}
