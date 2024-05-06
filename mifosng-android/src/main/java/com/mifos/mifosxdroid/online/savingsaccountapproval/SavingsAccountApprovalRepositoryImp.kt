package com.mifos.mifosxdroid.online.savingsaccountapproval

import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.core.objects.accounts.loan.SavingsApproval
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class SavingsAccountApprovalRepositoryImp @Inject constructor(private val dataManagerSavings: DataManagerSavings) :
    SavingsAccountApprovalRepository {

    override fun approveSavingsApplication(
        savingsAccountId: Int,
        savingsApproval: SavingsApproval?
    ): Observable<GenericResponse> {
        return dataManagerSavings.approveSavingsApplication(savingsAccountId, savingsApproval)
    }
}