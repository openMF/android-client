package com.mifos.mifosxdroid.activity.pinpointclient

import com.mifos.core.objects.client.ClientAddressResponse

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class PinPointClientUiState {

    object ShowProgressbar : PinPointClientUiState()

    data class ShowMessage(val message: Int) : PinPointClientUiState()

    object ShowFailedToFetchAddress : PinPointClientUiState()

    object ShowEmptyAddress : PinPointClientUiState()

    data class ShowClientPinpointLocations(val clientAddressResponses: List<ClientAddressResponse>) :
        PinPointClientUiState()

    data class ShowProgressDialog(val show: Boolean, val message: Int) : PinPointClientUiState()

    data class UpdateClientAddress(val genericResponse: Int) : PinPointClientUiState()


}
