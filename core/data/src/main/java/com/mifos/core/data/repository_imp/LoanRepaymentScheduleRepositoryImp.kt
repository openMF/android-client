package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.LoanRepaymentScheduleRepository
import com.mifos.core.network.DataManager
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class LoanRepaymentScheduleRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    LoanRepaymentScheduleRepository {

    override fun getLoanRepaySchedule(loanId: Int): Observable<LoanWithAssociations> {
        return dataManager.getLoanRepaySchedule(loanId)
    }
}