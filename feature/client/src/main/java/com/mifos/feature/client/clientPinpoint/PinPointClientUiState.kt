package com.mifos.feature.client.clientPinpoint

import com.mifos.core.objects.client.ClientAddressResponse

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class PinPointClientUiState {

    data object Loading : PinPointClientUiState()

    data class Error(val message: Int) : PinPointClientUiState()

    data class ClientPinpointLocations(val clientAddressResponses: List<ClientAddressResponse>) :
        PinPointClientUiState()

    data class SuccessMessage(val message: Int) : PinPointClientUiState()
}
