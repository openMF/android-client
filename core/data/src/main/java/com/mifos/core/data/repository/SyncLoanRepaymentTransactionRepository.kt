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

import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import rx.Observable

interface SyncLoanRepaymentTransactionRepository {

    fun databaseLoanRepayments(): Observable<List<LoanRepaymentRequest>>

    fun paymentTypeOption(): Observable<List<com.mifos.core.objects.PaymentTypeOption>>

    fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest,
    ): Observable<LoanRepaymentResponse>

    fun deleteAndUpdateLoanRepayments(loanId: Int): Observable<List<LoanRepaymentRequest>>

    fun updateLoanRepaymentTransaction(
        loanRepaymentRequest: LoanRepaymentRequest,
    ): Observable<LoanRepaymentRequest>
}
