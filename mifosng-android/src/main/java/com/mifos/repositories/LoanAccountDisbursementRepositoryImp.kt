package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.objects.accounts.loan.LoanDisbursement
import com.mifos.objects.templates.loans.LoanTransactionTemplate
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanAccountDisbursementRepositoryImp @Inject constructor(private val dataManagerLoan: DataManagerLoan) :
    LoanAccountDisbursementRepository {

    override fun getLoanTransactionTemplate(
        loanId: Int,
        command: String?
    ): Observable<LoanTransactionTemplate> {
        return dataManagerLoan.getLoanTransactionTemplate(loanId, command)
    }

    override fun disburseLoan(
        loanId: Int,
        loanDisbursement: LoanDisbursement?
    ): Observable<GenericResponse> {
        return disburseLoan(loanId, loanDisbursement)
    }


}