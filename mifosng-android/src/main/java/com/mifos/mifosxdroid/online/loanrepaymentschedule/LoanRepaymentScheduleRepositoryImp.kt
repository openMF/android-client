package com.mifos.mifosxdroid.online.loanrepaymentschedule

import com.mifos.api.DataManager
import com.mifos.objects.accounts.loan.LoanWithAssociations
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