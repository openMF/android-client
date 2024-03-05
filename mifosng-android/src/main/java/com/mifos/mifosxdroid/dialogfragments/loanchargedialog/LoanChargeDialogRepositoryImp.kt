package com.mifos.mifosxdroid.dialogfragments.loanchargedialog

import com.mifos.core.data.ChargesPayload
import com.mifos.core.network.DataManager
import com.mifos.core.objects.client.ChargeCreationResponse
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class LoanChargeDialogRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    LoanChargeDialogRepository {
    override fun getAllChargesV3(loanId: Int): Observable<ResponseBody> {
        return dataManager.getAllChargesV3(loanId)
    }

    override fun createLoanCharges(
        loanId: Int,
        chargesPayload: ChargesPayload?
    ): Observable<ChargeCreationResponse> {
        return dataManager.createLoanCharges(loanId, chargesPayload)
    }
}