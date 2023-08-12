package com.mifos.repositories

import com.mifos.api.DataManager
import com.mifos.objects.accounts.loan.LoanWithAssociations
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