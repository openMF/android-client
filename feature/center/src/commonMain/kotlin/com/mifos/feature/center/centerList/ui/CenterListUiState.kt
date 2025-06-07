/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.centerList.ui

import androidx.paging.PagingData
import com.mifos.room.entities.group.CenterEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.StringResource

sealed class CenterListUiState {

    data object Loading : CenterListUiState()

    data class Error(val message: StringResource) : CenterListUiState()

    data class CenterList(val centers: Flow<PagingData<CenterEntity>>) : CenterListUiState()

    data class CenterListDb(val centers: List<CenterEntity>?) : CenterListUiState()
}
