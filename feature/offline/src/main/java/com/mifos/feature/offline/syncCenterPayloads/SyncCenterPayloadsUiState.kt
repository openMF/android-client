/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncCenterPayloads

import com.mifos.room.entities.center.CenterPayload

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncCenterPayloadsUiState {

    data object ShowProgressbar : SyncCenterPayloadsUiState()

    data class ShowError(val message: String) : SyncCenterPayloadsUiState()

    data class ShowCenters(val centerPayloads: List<CenterPayload>) : SyncCenterPayloadsUiState()
}
