package com.mifos.core.data.repository

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.LoanApproval
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface LoanAccountApprovalRepository {

    fun approveLoan(loanId: Int, loanApproval: LoanApproval?): Observable<GenericResponse>

}