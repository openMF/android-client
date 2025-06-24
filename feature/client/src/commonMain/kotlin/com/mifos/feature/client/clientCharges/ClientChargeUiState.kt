/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientCharges

import androidx.paging.PagingData
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.StringResource

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class ClientChargeUiState {

    data object Loading : ClientChargeUiState()

    data class Error(val message: StringResource) : ClientChargeUiState()

    data class ChargesList(val chargesPage: Flow<PagingData<ChargesEntity>>) : ClientChargeUiState()
}
