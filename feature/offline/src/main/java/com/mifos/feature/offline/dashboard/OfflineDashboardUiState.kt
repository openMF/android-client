package com.mifos.feature.offline.dashboard

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class OfflineDashboardUiState {
    data class SyncUiState(val list : List<SyncStateData>) : OfflineDashboardUiState()
}

data class SyncStateData(
    var count: Int = 0,
    val name : Int = -1,
    val type : Type,
    var errorMsg : String? = null
)