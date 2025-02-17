/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.syncGroupDialog

import androidx.compose.ui.graphics.vector.ImageVector
import com.mifos.room.entities.group.Group

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncGroupsDialogUiState {
    data object Loading : SyncGroupsDialogUiState()
    data object Success : SyncGroupsDialogUiState()
    data class Error(
        val messageResId: Int? = null,
        val imageVector: ImageVector? = null,
        val message: String? = null,
    ) : SyncGroupsDialogUiState()
}

data class SyncGroupDialogData(
    val totalSyncCount: Int = 0,
    val groupName: String = "",
    val isSyncSuccess: Boolean = false,
    val singleSyncCount: Int = 0,
    val totalClientSyncCount: Int = 0,
    val clientSyncCount: Int = 0,
    val failedSyncGroupCount: Int = 0,
    val groupList: List<Group> = listOf(),
)
