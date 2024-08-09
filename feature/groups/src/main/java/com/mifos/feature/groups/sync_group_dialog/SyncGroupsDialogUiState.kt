package com.mifos.feature.groups.sync_group_dialog

import androidx.compose.ui.graphics.vector.ImageVector
import com.mifos.core.objects.group.Group

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncGroupsDialogUiState {
    data object Loading: SyncGroupsDialogUiState()
    data object Success: SyncGroupsDialogUiState()
    data class Error(
        val messageResId: Int? = null,
        val imageVector: ImageVector? = null,
        val message: String? = null
    ): SyncGroupsDialogUiState()
}

data class SyncGroupDialogData(
    val totalSyncCount: Int = 0,
    val groupName: String = "",
    val isSyncSuccess: Boolean = false,
    val singleSyncCount: Int = 0,
    val totalClientSyncCount: Int = 0,
    val clientSyncCount: Int = 0,
    val failedSyncGroupCount: Int = 0,
    val groupList: List<Group> = listOf()
)