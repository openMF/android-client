package com.mifos.mifosxdroid.online.loanaccount

import com.mifos.core.data.LoansPayload
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.LoanTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface LoanAccountRepository {

    fun allLoans(): Observable<List<LoanProducts>>

    fun getLoansAccountTemplate(clientId: Int, productId: Int): Observable<LoanTemplate>

    fun createLoansAccount(loansPayload: LoansPayload?): Observable<Loans>


}