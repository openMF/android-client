package com.mifos.feature.client.sync_client_dialog

import androidx.compose.ui.graphics.vector.ImageVector
import com.mifos.core.objects.client.Client

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncClientsDialogUiState {

    data object Loading : SyncClientsDialogUiState()
    data object Success : SyncClientsDialogUiState()
    data class Error(
        val messageResId: Int? = null,
        val imageVector: ImageVector? = null,
        val message: String? = null
    ) : SyncClientsDialogUiState()
}

data class SyncClientsDialogData(
    val totalSyncCount: Int = 0,
    val clientName: String = "",
    val isSyncSuccess: Boolean = false,
    val singleSyncCount: Int = 0,
    val totalClientSyncCount: Int = 0,
    val clientSyncCount: Int = 0,
    val failedSyncGroupCount: Int = 0,
    val clientList: List<Client> = listOf()
)