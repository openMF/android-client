package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.objects.accounts.loan.SavingsApproval
import rx.Observable

interface SavingsAccountApprovalRepository {

    fun approveSavingsApplication(
        savingsAccountId: Int,
        savingsApproval: SavingsApproval?
    ): Observable<GenericResponse>

}