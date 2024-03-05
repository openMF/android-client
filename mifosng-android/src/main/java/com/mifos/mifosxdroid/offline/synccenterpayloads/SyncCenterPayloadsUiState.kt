package com.mifos.mifosxdroid.offline.synccenterpayloads

import com.mifos.core.data.CenterPayload

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncCenterPayloadsUiState {

    object ShowProgressbar : SyncCenterPayloadsUiState()

    data class ShowError(val message: String) : SyncCenterPayloadsUiState()

    data class ShowCenters(val centerPayloads: List<CenterPayload>) : SyncCenterPayloadsUiState()

    data class ShowCenterSyncFailed(val message: String) : SyncCenterPayloadsUiState()

    object ShowCenterSyncResponse : SyncCenterPayloadsUiState()

    data class ShowPayloadDeletedAndUpdatePayloads(val centerPayloads: List<CenterPayload>) :
        SyncCenterPayloadsUiState()

    data class ShowCenterPayloadUpdated(val centerPayload: CenterPayload) :
        SyncCenterPayloadsUiState()
}