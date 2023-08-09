package com.mifos.repositories

import com.mifos.api.DataManager
import com.mifos.objects.client.Charges
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanChargeRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    LoanChargeRepository {

    override fun getListOfLoanCharges(loanId: Int): Observable<List<Charges>> {
        return dataManager.getListOfLoanCharges(loanId)
    }
}