package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class LoanAccountSummaryRepositoryImp @Inject constructor(private val dataManagerLoan: DataManagerLoan) :
    LoanAccountSummaryRepository {

    override fun getLoanById(loanId: Int): Observable<LoanWithAssociations> {
        return dataManagerLoan.getLoanById(loanId)
    }
}