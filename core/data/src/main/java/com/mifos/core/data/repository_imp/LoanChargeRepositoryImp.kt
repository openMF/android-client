package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.LoanChargeRepository
import com.mifos.core.network.DataManager
import com.mifos.core.objects.client.Charges
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class LoanChargeRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    LoanChargeRepository {

    override suspend fun getListOfLoanCharges(loanId: Int): List<Charges> {
        return dataManager.getListOfLoanCharges(loanId)
    }
}