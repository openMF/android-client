package com.mifos.core.data.repository

import com.mifos.core.data.ChargesPayload
import com.mifos.core.objects.client.ChargeCreationResponse
import okhttp3.ResponseBody

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface LoanChargeDialogRepository {

    suspend fun getAllChargesV3(loanId: Int): ResponseBody

    suspend fun createLoanCharges(
        loanId: Int,
        chargesPayload: ChargesPayload
    ): ChargeCreationResponse
}