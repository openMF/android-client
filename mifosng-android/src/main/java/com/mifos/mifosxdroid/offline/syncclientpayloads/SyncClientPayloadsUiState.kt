package com.mifos.mifosxdroid.offline.syncclientpayloads

import com.mifos.core.objects.client.ClientPayload

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncClientPayloadsUiState {

    data object ShowProgressbar : SyncClientPayloadsUiState()

    data class ShowError(val message: String) : SyncClientPayloadsUiState()

    data class ShowPayloads(val clientPayloads: List<ClientPayload>) : SyncClientPayloadsUiState()

}