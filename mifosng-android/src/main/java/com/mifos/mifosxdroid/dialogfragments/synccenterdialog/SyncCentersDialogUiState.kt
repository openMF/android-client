package com.mifos.mifosxdroid.dialogfragments.synccenterdialog

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncCentersDialogUiState {

    data object ShowCentersSyncSuccessfully : SyncCentersDialogUiState()

    data object ShowNetworkIsNotAvailable : SyncCentersDialogUiState()

    data object DismissDialog : SyncCentersDialogUiState()

    data class UpdateSingleSyncCenterProgressBar(val total: Int) : SyncCentersDialogUiState()

    data class ShowSyncedFailedCenters(val size: Int) : SyncCentersDialogUiState()

    data class ShowError(val message: String) : SyncCentersDialogUiState()

    data class UpdateGroupSyncProgressBar(val index: Int) : SyncCentersDialogUiState()

    object ShowProgressbar : SyncCentersDialogUiState()

    data class SetGroupSyncProgressBarMax(val size: Int) : SyncCentersDialogUiState()

    data class SetClientSyncProgressBarMax(val total: Int) : SyncCentersDialogUiState()

    data class UpdateClientSyncProgressBar(val index: Int) : SyncCentersDialogUiState()

    data class UpdateTotalSyncCenterProgressBarAndCount(val total: Int) : SyncCentersDialogUiState()

    data class ShowSyncingCenter(val centerName: String) : SyncCentersDialogUiState()
}
