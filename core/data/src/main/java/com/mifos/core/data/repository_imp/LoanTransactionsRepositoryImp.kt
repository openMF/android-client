package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.LoanTransactionsRepository
import com.mifos.core.network.DataManager
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class LoanTransactionsRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    LoanTransactionsRepository {

    override fun getLoanTransactions(loan: Int): Observable<LoanWithAssociations> {
        return dataManager.getLoanTransactions(loan)
    }
}