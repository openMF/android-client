package com.mifos.feature.center.sync_center_payloads

import com.mifos.core.data.CenterPayload

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncCenterPayloadsUiState {

    data object ShowProgressbar : SyncCenterPayloadsUiState()

    data class ShowError(val message: String) : SyncCenterPayloadsUiState()

    data class ShowCenters(val centerPayloads: List<CenterPayload>) : SyncCenterPayloadsUiState()
}