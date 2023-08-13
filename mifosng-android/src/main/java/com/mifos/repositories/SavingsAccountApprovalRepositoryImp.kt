package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.objects.accounts.loan.SavingsApproval
import rx.Observable
import javax.inject.Inject

class SavingsAccountApprovalRepositoryImp @Inject constructor(private val dataManagerSavings: DataManagerSavings) :
    SavingsAccountApprovalRepository {

    override fun approveSavingsApplication(
        savingsAccountId: Int,
        savingsApproval: SavingsApproval?
    ): Observable<GenericResponse> {
        return dataManagerSavings.approveSavingsApplication(savingsAccountId, savingsApproval)
    }
}