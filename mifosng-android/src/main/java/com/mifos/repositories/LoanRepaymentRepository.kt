package com.mifos.repositories

import com.mifos.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.objects.templates.loans.LoanRepaymentTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface LoanRepaymentRepository {

    fun getLoanRepayTemplate(loanId: Int): Observable<LoanRepaymentTemplate>

    fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest
    ): Observable<LoanRepaymentResponse>

    fun getDatabaseLoanRepaymentByLoanId(loanId: Int): Observable<LoanRepaymentRequest>

}