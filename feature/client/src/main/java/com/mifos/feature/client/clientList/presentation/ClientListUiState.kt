/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientList.presentation

import androidx.paging.PagingData
import com.mifos.core.dbobjects.client.Client
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class ClientListUiState {

    data object Empty : ClientListUiState()

    data class Error(val message: String) : ClientListUiState()

    data class ClientListApi(val list: Flow<PagingData<Client>>) : ClientListUiState()

    data class ClientListDb(val list: List<Client>) : ClientListUiState()
}
