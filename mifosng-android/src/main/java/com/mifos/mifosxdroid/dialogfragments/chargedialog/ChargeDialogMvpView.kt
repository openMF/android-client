package com.mifos.mifosxdroid.dialogfragments.chargedialog

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.client.ChargeCreationResponse
import com.mifos.objects.templates.clients.ChargeTemplate

/**
 * Created by Rajan Maurya on 08/06/16.
 */
interface ChargeDialogMvpView : MvpView {
    fun showAllChargesV2(chargeTemplate: ChargeTemplate)
    fun showChargesCreatedSuccessfully(chargeCreationResponse: ChargeCreationResponse)
    fun showChargeCreatedFailure(errorMessage: String)
    fun showFetchingError(s: String)
}