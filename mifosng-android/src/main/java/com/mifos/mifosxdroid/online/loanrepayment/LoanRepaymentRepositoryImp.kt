package com.mifos.mifosxdroid.online.loanrepayment

import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.objects.templates.loans.LoanRepaymentTemplate
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanRepaymentRepositoryImp @Inject constructor(private val dataManagerLoan: DataManagerLoan) :
    LoanRepaymentRepository {

    override fun getLoanRepayTemplate(loanId: Int): Observable<LoanRepaymentTemplate> {
        return dataManagerLoan.getLoanRepayTemplate(loanId)
    }

    override fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest
    ): Observable<LoanRepaymentResponse> {
        return submitPayment(loanId, request)
    }

    override fun getDatabaseLoanRepaymentByLoanId(loanId: Int): Observable<LoanRepaymentRequest> {
        return dataManagerLoan.getDatabaseLoanRepaymentByLoanId(loanId)
    }


}