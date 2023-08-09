package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.objects.accounts.loan.LoanDisbursement
import com.mifos.objects.templates.loans.LoanTransactionTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface LoanAccountDisbursementRepository {

    fun getLoanTransactionTemplate(
        loanId: Int,
        command: String?
    ): Observable<LoanTransactionTemplate>

    fun disburseLoan(
        loanId: Int,
        loanDisbursement: LoanDisbursement?
    ): Observable<GenericResponse>

}