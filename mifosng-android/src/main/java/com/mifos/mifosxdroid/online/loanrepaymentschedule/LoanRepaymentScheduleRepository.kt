package com.mifos.mifosxdroid.online.loanrepaymentschedule

import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import rx.Observable

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface LoanRepaymentScheduleRepository {

    fun getLoanRepaySchedule(loanId: Int): Observable<LoanWithAssociations>

}