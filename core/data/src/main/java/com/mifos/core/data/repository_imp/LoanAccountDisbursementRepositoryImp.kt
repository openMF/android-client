package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.LoanAccountDisbursementRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.objects.accounts.loan.LoanDisbursement
import com.mifos.core.objects.templates.loans.LoanTransactionTemplate
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