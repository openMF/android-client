package com.mifos.core.data.repository

import com.mifos.core.objects.client.Charges

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface LoanChargeRepository {

    suspend fun getListOfLoanCharges(loanId: Int): List<Charges>

}