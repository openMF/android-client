package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncClientsDialogUiState {

    data object ShowClientsSyncSuccessfully : SyncClientsDialogUiState()

    data object ShowNetworkIsNotAvailable : SyncClientsDialogUiState()

    data object DismissDialog : SyncClientsDialogUiState()

    data class UpdateSingleSyncClientProgressBar(val total: Int) : SyncClientsDialogUiState()

    data class ShowSyncedFailedClients(val total: Int) : SyncClientsDialogUiState()

    data class ShowError(val message: String) : SyncClientsDialogUiState()

    data class UpdateTotalSyncClientProgressBarAndCount(val total: Int) : SyncClientsDialogUiState()

    data class ShowSyncingClient(val clientName: String) : SyncClientsDialogUiState()
}