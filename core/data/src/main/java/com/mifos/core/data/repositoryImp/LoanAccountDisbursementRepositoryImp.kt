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

import com.mifos.core.data.repository.LoanAccountDisbursementRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.modelobjects.account.loan.LoanDisbursement
import com.mifos.core.objects.templates.loans.LoanTransactionTemplate
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanAccountDisbursementRepositoryImp @Inject constructor(private val dataManagerLoan: DataManagerLoan) :
    LoanAccountDisbursementRepository {

    override fun getLoanTransactionTemplate(
        loanId: Int,
        command: String?,
    ): Observable<LoanTransactionTemplate> {
        return dataManagerLoan.getLoanTransactionTemplate(loanId, command)
    }

    override fun disburseLoan(
        loanId: Int,
        loanDisbursement: LoanDisbursement?,
    ): Observable<GenericResponse> {
        return disburseLoan(loanId, loanDisbursement)
    }
}
