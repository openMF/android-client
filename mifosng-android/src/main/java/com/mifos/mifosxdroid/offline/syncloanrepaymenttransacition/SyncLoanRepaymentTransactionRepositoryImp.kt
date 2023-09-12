package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition

import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.objects.accounts.loan.LoanRepaymentResponse
import rx.Observable
import javax.inject.Inject

class SyncLoanRepaymentTransactionRepositoryImp @Inject constructor(private val dataManagerLoan: DataManagerLoan) :
    SyncLoanRepaymentTransactionRepository {

    override fun databaseLoanRepayments(): Observable<List<LoanRepaymentRequest>> {
        return dataManagerLoan.databaseLoanRepayments
    }

    override fun paymentTypeOption(): Observable<List<PaymentTypeOption>> {
        return dataManagerLoan.paymentTypeOption
    }

    override fun submitPayment(
        loanId: Int,
        request: LoanRepaymentRequest
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