package com.mifos.mifosxdroid.online.grouploanaccount

import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.network.DataManager
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.GroupLoanTemplate
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class GroupLoanAccountRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    GroupLoanAccountRepository {

    override fun allLoans(): Observable<List<LoanProducts>> {
        return dataManager.allLoans
    }

    override fun getGroupLoansAccountTemplate(
        groupId: Int,
        productId: Int
    ): Observable<GroupLoanTemplate> {
        return dataManager.getGroupLoansAccountTemplate(groupId, productId)
    }

    override fun createGroupLoansAccount(loansPayload: GroupLoanPayload?): Observable<Loans> {
        return dataManager.createGroupLoansAccount(loansPayload)
    }
}