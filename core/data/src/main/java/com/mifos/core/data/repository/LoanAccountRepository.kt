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

import com.mifos.core.entity.accounts.loan.Loans
import com.mifos.core.entity.templates.loans.LoanTemplate
import com.mifos.core.network.model.LoansPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface LoanAccountRepository {

    suspend fun allLoans(): Observable<List<com.mifos.core.model.objects.organisations.LoanProducts>>

    suspend fun getLoansAccountTemplate(clientId: Int, productId: Int): Observable<LoanTemplate>

    suspend fun createLoansAccount(loansPayload: LoansPayload): Observable<Loans>
}
