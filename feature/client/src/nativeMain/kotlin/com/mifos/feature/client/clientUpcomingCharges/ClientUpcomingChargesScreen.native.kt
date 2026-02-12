/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientUpcomingCharges

import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow

@Composable
actual fun ChargesListContent(
    charges: Flow<PagingData<ChargesEntity>>,
    state: ClientUpcomingChargesState,
    onAction: (ClientUpcomingChargesAction) -> Unit,
    setCount: (Int) -> Unit,
    refresh: () -> Unit,
) {
}
