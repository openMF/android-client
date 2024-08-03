package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
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