package com.mifos.mifosxdroid.dialogfragments.chargedialog

import com.mifos.core.objects.client.ChargeCreationResponse
import com.mifos.core.objects.templates.clients.ChargeTemplate

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class ChargeDialogUiState {

    data object ShowProgressbar : ChargeDialogUiState()

    data class ShowFetchingError(val message: String) : ChargeDialogUiState()

    data class ShowAllChargesV2(val chargeTemplate: ChargeTemplate) : ChargeDialogUiState()

    data class ShowChargesCreatedSuccessfully(val chargeCreationResponse: ChargeCreationResponse) :
        ChargeDialogUiState()
}