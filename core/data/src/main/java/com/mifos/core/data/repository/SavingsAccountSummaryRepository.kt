package com.mifos.core.data.repository

import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface SavingsAccountSummaryRepository {

    fun getSavingsAccount(
        type: String?,
        savingsAccountId: Int,
        association: String?
    ): Observable<SavingsAccountWithAssociations>
}