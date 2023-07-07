package com.mifos.mifosxdroid.dialogfragments.loanchargedialog

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.client.ChargeCreationResponse
import okhttp3.ResponseBody

/**
 * Created by Rajan Maurya on 9/6/16.
 */
interface LoanChargeDialogMvpView : MvpView {
    fun showAllChargesV3(response: ResponseBody)
    fun showLoanChargesCreatedSuccessfully(chargeCreationResponse: ChargeCreationResponse)
    fun showChargeCreatedFailure(errorMessage: String)
    fun showError(errorMessage: String)
}