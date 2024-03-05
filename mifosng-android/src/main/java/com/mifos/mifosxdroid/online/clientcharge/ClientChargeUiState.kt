package com.mifos.mifosxdroid.online.clientcharge

import com.mifos.core.objects.client.Charges
import com.mifos.core.objects.client.Page

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class ClientChargeUiState {

    data object ShowProgressbar : ClientChargeUiState()

    data class ShowFetchingErrorCharges(val message: String) : ClientChargeUiState()

    data class ShowChargesList(val chargesPage: Page<Charges>) : ClientChargeUiState()

    data object ShowEmptyCharges : ClientChargeUiState()
}
