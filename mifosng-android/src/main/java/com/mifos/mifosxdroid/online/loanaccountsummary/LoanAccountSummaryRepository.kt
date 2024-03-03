package com.mifos.mifosxdroid.online.loanaccountsummary

import com.mifos.objects.accounts.loan.LoanWithAssociations
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface LoanAccountSummaryRepository {

    fun getLoanById(loanId: Int): Observable<LoanWithAssociations>

}