package com.mifos.feature.center.sync_centers_dialog

import androidx.compose.ui.graphics.vector.ImageVector
import com.mifos.core.objects.group.Center

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncCentersDialogUiState {
    data object Loading : SyncCentersDialogUiState()
    data object Success : SyncCentersDialogUiState()
    data class Error(
        val messageResId: Int? = null,
        val imageVector: ImageVector? = null,
        val message: String? = null
    ) : SyncCentersDialogUiState()
}

data class SyncCentersDialogData(
    val totalSyncCount: Int = 0,
    val centerName: String = "",
    val isSyncSuccess: Boolean = false,
    val singleSyncCount: Int = 0,
    val totalClientSyncCount: Int = 0,
    val totalGroupsSyncCount: Int = 0,
    val clientSyncCount: Int = 0,
    val failedSyncGroupCount: Int = 0,
    val centersList: List<Center> = listOf()
)
