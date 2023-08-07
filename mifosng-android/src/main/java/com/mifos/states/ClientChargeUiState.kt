package com.mifos.states

import com.mifos.objects.client.Charges
import com.mifos.objects.client.Page

sealed class ClientChargeUiState {

    object ShowProgressbar : ClientChargeUiState()

    data class ShowFetchingErrorCharges(val message : String) : ClientChargeUiState()

    data class ShowChargesList(val chargesPage: Page<Charges>) : ClientChargeUiState()

    object ShowEmptyCharges : ClientChargeUiState()
}
