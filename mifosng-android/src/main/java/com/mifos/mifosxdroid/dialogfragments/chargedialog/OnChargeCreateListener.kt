package com.mifos.mifosxdroid.dialogfragments.chargedialog

import com.mifos.core.objects.client.Charges

/**
 * Created by Tarun on 13-08-17.
 */
interface OnChargeCreateListener {
    fun onChargeCreatedSuccess(charge: Charges)
    fun onChargeCreatedFailure(errorMessage: String)
}