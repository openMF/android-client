package com.mifos.repositories

import com.mifos.objects.client.Charges
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface LoanChargeRepository {

    fun getListOfLoanCharges(loanId: Int): Observable<List<Charges>>

}