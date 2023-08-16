package com.mifos.repositories

import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.objects.accounts.loan.LoanRepaymentResponse
import rx.Observable

interface SyncLoanRepaymentTransactionRepository {

    fun databaseLoanRepayments(): Observable<List<LoanRepaymentRequest>>

    fun paymentTypeOption(): Observable<List<PaymentTypeOption>>

    fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest
    ): Observable<LoanRepaymentResponse>

    fun deleteAndUpdateLoanRepayments(loanId: Int): Observable<List<LoanRepaymentRequest>>

    fun updateLoanRepaymentTransaction(
        loanRepaymentRequest: LoanRepaymentRequest
    ): Observable<LoanRepaymentRequest>

}