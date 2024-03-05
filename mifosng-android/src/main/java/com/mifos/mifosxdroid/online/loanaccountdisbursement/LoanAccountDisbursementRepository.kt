package com.mifos.mifosxdroid.online.loanaccountdisbursement

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.LoanDisbursement
import com.mifos.core.objects.templates.loans.LoanTransactionTemplate
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