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

import com.mifos.core.entity.templates.loans.LoanTransactionTemplate
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.account.loan.LoanDisbursement
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface LoanAccountDisbursementRepository {

    fun getLoanTransactionTemplate(
        loanId: Int,
        command: String?,
    ): Observable<LoanTransactionTemplate>

    fun disburseLoan(
        loanId: Int,
        loanDisbursement: LoanDisbursement?,
    ): Observable<GenericResponse>
}
