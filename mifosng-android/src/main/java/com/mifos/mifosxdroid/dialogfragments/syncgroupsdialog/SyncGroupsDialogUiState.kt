package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncGroupsDialogUiState {

    data object ShowGroupsSyncSuccessfully : SyncGroupsDialogUiState()

    data object ShowNetworkIsNotAvailable : SyncGroupsDialogUiState()

    data object DismissDialog : SyncGroupsDialogUiState()

    data class UpdateSingleSyncGroupProgressBar(val index: Int) : SyncGroupsDialogUiState()

    data class ShowSyncedFailedGroups(val size: Int) : SyncGroupsDialogUiState()

    object ShowProgressbar : SyncGroupsDialogUiState()

    data class SetClientSyncProgressBarMax(val size: Int) : SyncGroupsDialogUiState()

    data class ShowError(val message: String) : SyncGroupsDialogUiState()

    data class UpdateClientSyncProgressBar(val index: Int) : SyncGroupsDialogUiState()

    data class UpdateTotalSyncGroupProgressBarAndCount(val total: Int) : SyncGroupsDialogUiState()

    data class ShowSyncingGroup(val groupName: String) : SyncGroupsDialogUiState()
}