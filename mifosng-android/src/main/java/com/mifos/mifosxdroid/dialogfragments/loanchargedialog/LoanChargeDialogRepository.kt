package com.mifos.mifosxdroid.dialogfragments.loanchargedialog

import com.mifos.core.data.ChargesPayload
import com.mifos.core.objects.client.ChargeCreationResponse
import okhttp3.ResponseBody
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface LoanChargeDialogRepository {

    fun getAllChargesV3(loanId: Int): Observable<ResponseBody>

    fun createLoanCharges(
        loanId: Int,
        chargesPayload: ChargesPayload?
    ): Observable<ChargeCreationResponse>
}