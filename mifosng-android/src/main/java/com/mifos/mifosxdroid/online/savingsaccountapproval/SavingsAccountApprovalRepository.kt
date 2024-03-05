package com.mifos.mifosxdroid.online.savingsaccountapproval

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.SavingsApproval
import rx.Observable

/**
 * Created by Aditya Gupta on 13/08/23.
 */
interface SavingsAccountApprovalRepository {

    fun approveSavingsApplication(
        savingsAccountId: Int,
        savingsApproval: SavingsApproval?
    ): Observable<GenericResponse>

}