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

import com.mifos.core.data.LoansPayload
import com.mifos.core.data.repository.LoanAccountRepository
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.modelobjects.organisations.LoanProducts
import com.mifos.core.objects.templates.loans.LoanTemplate
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class LoanAccountRepositoryImp @Inject constructor(private val dataManagerLoan: DataManagerLoan) :
    LoanAccountRepository {

    override suspend fun allLoans(): Observable<List<LoanProducts>> {
        return dataManagerLoan.allLoans
    }

    override suspend fun getLoansAccountTemplate(
        clientId: Int,
        productId: Int,
    ): Observable<LoanTemplate> {
        return dataManagerLoan.getLoansAccountTemplate(clientId, productId)
    }

    override suspend fun createLoansAccount(loansPayload: LoansPayload): Observable<Loans> {
        return dataManagerLoan.createLoansAccount(loansPayload)
    }
}
