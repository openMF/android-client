/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientsList

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.Flow

@Composable
internal actual fun LazyColumnForClientListApi(
    pagingFlow: Flow<PagingData<ClientEntity>>,
    onRefresh: () -> Unit,
    onClientSelect: (Int) -> Unit,
    fetchImage: (Int) -> Unit,
    images: Map<Int, ByteArray?>,
    modifier: Modifier,
) {
}
