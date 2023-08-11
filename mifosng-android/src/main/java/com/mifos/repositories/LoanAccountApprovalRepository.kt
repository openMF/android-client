package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.objects.accounts.loan.LoanApproval
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface LoanAccountApprovalRepository {

    fun approveLoan(loanId: Int, loanApproval: LoanApproval?): Observable<GenericResponse>

}