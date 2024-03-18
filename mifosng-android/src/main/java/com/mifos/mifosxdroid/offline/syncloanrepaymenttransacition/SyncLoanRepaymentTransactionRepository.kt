package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition

import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import rx.Observable

interface SyncLoanRepaymentTransactionRepository {

    fun databaseLoanRepayments(): Observable<List<LoanRepaymentRequest>>

    fun paymentTypeOption(): Observable<List<com.mifos.core.objects.PaymentTypeOption>>

    fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest
    ): Observable<LoanRepaymentResponse>

    fun deleteAndUpdateLoanRepayments(loanId: Int): Observable<List<LoanRepaymentRequest>>

    fun updateLoanRepaymentTransaction(
        loanRepaymentRequest: LoanRepaymentRequest
    ): Observable<LoanRepaymentRequest>

}