package com.mifos.states

import com.mifos.objects.client.ClientPayload

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncClientPayloadsUiState {

    object ShowProgressbar : SyncClientPayloadsUiState()

    data class ShowError(val message: String) : SyncClientPayloadsUiState()

    data class ShowPayloads(val clientPayloads: List<ClientPayload>) : SyncClientPayloadsUiState()

    object ShowSyncResponse : SyncClientPayloadsUiState()

    data class ShowPayloadDeletedAndUpdatePayloads(val clientPayloads: List<ClientPayload>) :
        SyncClientPayloadsUiState()

    data class ShowClientPayloadUpdated(val clientPayload: ClientPayload) :
        SyncClientPayloadsUiState()
}