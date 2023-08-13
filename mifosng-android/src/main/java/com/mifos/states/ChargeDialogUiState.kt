package com.mifos.states

import com.mifos.objects.client.ChargeCreationResponse
import com.mifos.objects.templates.clients.ChargeTemplate

sealed class ChargeDialogUiState {

    object ShowProgressbar : ChargeDialogUiState()

    data class ShowFetchingError(val message : String) : ChargeDialogUiState()

    data class ShowAllChargesV2(val chargeTemplate: ChargeTemplate) : ChargeDialogUiState()

    data class ShowChargesCreatedSuccessfully(val chargeCreationResponse: ChargeCreationResponse) : ChargeDialogUiState()
}