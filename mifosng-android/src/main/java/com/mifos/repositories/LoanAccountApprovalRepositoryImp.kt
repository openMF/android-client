package com.mifos.repositories

import com.mifos.api.DataManager
import com.mifos.api.GenericResponse
import com.mifos.objects.accounts.loan.LoanApproval
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanAccountApprovalRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    LoanAccountApprovalRepository {

    override fun approveLoan(
        loanId: Int,
        loanApproval: LoanApproval?
    ): Observable<GenericResponse> {
        return dataManager.approveLoan(loanId, loanApproval)
    }
}