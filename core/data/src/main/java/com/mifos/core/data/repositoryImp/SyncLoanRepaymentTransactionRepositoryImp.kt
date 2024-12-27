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

import com.mifos.core.data.repository.SyncLoanRepaymentTransactionRepository
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.modelobjects.account.loan.LoanRepaymentResponse
import rx.Observable
import javax.inject.Inject

class SyncLoanRepaymentTransactionRepositoryImp @Inject constructor(private val dataManagerLoan: DataManagerLoan) :
    SyncLoanRepaymentTransactionRepository {

    override fun databaseLoanRepayments(): Observable<List<LoanRepaymentRequest>> {
        return dataManagerLoan.databaseLoanRepayments
    }

    override fun paymentTypeOption(): Observable<List<com.mifos.core.objects.PaymentTypeOption>> {
        return dataManagerLoan.paymentTypeOption
    }

    override fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest,
    ): Observable<LoanRepaymentResponse> {
        return dataManagerLoan.submitPayment(loanId, request)
    }

    override fun deleteAndUpdateLoanRepayments(loanId: Int): Observable<List<LoanRepaymentRequest>> {
        return dataManagerLoan.deleteAndUpdateLoanRepayments(loanId)
    }

    override fun updateLoanRepaymentTransaction(loanRepaymentRequest: LoanRepaymentRequest): Observable<LoanRepaymentRequest> {
        return dataManagerLoan.updateLoanRepaymentTransaction(loanRepaymentRequest)
    }
}
