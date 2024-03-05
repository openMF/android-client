package com.mifos.mifosxdroid.offline.syncgrouppayloads

import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.response.SaveResponse

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncGroupPayloadsUiState {

    object ShowProgressbar : SyncGroupPayloadsUiState()

    data class ShowError(val message: Int) : SyncGroupPayloadsUiState()

    data class ShowGroups(val groupPayloads: List<GroupPayload>) : SyncGroupPayloadsUiState()

    data class ShowGroupSyncFailed(val message: String) : SyncGroupPayloadsUiState()

    data class ShowGroupSyncResponse(val group: SaveResponse) : SyncGroupPayloadsUiState()

    data class ShowPayloadDeletedAndUpdatePayloads(val groupPayloads: List<GroupPayload>) :
        SyncGroupPayloadsUiState()

    data class ShowGroupPayloadUpdated(val groupPayload: GroupPayload) : SyncGroupPayloadsUiState()
}
