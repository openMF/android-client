package com.mifos.repositories

import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.organisation.LoanProducts
import com.mifos.objects.templates.loans.LoanTemplate
import com.mifos.services.data.LoansPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface LoanAccountRepository {

    fun allLoans(): Observable<List<LoanProducts>>

    fun getLoansAccountTemplate(clientId: Int, productId: Int): Observable<LoanTemplate>

    fun createLoansAccount(loansPayload: LoansPayload?): Observable<Loans>


}