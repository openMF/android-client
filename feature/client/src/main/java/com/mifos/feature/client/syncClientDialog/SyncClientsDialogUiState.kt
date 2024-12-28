/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.syncClientDialog

import androidx.compose.ui.graphics.vector.ImageVector
import com.mifos.core.dbobjects.client.Client

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncClientsDialogUiState {

    data object Loading : SyncClientsDialogUiState()
    data object Success : SyncClientsDialogUiState()
    data class Error(
        val messageResId: Int? = null,
        val imageVector: ImageVector? = null,
        val message: String? = null,
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
    val clientList: List<Client> = listOf(),
)
