package com.mifos.core.data.repository

import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface LoanAccountSummaryRepository {

    fun getLoanById(loanId: Int): Observable<LoanWithAssociations>

}