package com.mifos.feature.client.client_charge_dialog

import com.mifos.core.objects.templates.clients.ChargeTemplate

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class ChargeDialogUiState {

    data object Loading : ChargeDialogUiState()

    data class Error(val message: Int) : ChargeDialogUiState()

    data class AllChargesV2(val chargeTemplate: ChargeTemplate) : ChargeDialogUiState()

    data object ChargesCreatedSuccessfully : ChargeDialogUiState()
}