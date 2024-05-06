package com.mifos.mifosxdroid.online.loanaccount

import com.mifos.core.data.LoansPayload
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.LoanTemplate
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class LoanAccountRepositoryImp @Inject constructor(private val dataManagerLoan: DataManagerLoan) :
    LoanAccountRepository {

    override fun allLoans(): Observable<List<LoanProducts>> {
        return dataManagerLoan.allLoans
    }

    override fun getLoansAccountTemplate(clientId: Int, productId: Int): Observable<LoanTemplate> {
        return dataManagerLoan.getLoansAccountTemplate(clientId, productId)
    }

    override fun createLoansAccount(loansPayload: LoansPayload?): Observable<Loans> {
        return dataManagerLoan.createLoansAccount(loansPayload)
    }
}