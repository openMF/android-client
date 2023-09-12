package com.mifos.mifosxdroid.online.loanaccount

import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.organisation.LoanProducts
import com.mifos.objects.templates.loans.LoanTemplate
import com.mifos.services.data.LoansPayload
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