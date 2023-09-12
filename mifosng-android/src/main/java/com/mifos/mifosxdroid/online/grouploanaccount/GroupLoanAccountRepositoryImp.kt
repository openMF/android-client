package com.mifos.mifosxdroid.online.grouploanaccount

import com.mifos.api.DataManager
import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.organisation.LoanProducts
import com.mifos.objects.templates.loans.GroupLoanTemplate
import com.mifos.services.data.GroupLoanPayload
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