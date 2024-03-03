package com.mifos.mifosxdroid.online.grouploanaccount

import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.organisation.LoanProducts
import com.mifos.objects.templates.loans.GroupLoanTemplate
import com.mifos.services.data.GroupLoanPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface GroupLoanAccountRepository {

    fun allLoans(): Observable<List<LoanProducts>>

    fun getGroupLoansAccountTemplate(groupId: Int, productId: Int): Observable<GroupLoanTemplate>

    fun createGroupLoansAccount(loansPayload: GroupLoanPayload?): Observable<Loans>
}