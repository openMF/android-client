package com.mifos.mifosxdroid.online.savingaccountsummary

import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SavingsAccountSummaryRepositoryImp @Inject constructor(private val dataManagerSavings: DataManagerSavings) :
    SavingsAccountSummaryRepository {
    override fun getSavingsAccount(
        type: String?,
        savingsAccountId: Int,
        association: String?
    ): Observable<SavingsAccountWithAssociations> {
        return dataManagerSavings.getSavingsAccount(type, savingsAccountId, association)
    }
}