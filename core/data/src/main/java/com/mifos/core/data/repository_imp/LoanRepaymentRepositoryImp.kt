package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
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