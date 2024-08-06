package com.mifos.core.data.repository

import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import rx.Observable

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface LoanTransactionsRepository {

    fun getLoanTransactions(loan: Int): Observable<LoanWithAssociations>

}