/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.dashboard

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class OfflineDashboardUiState {
    data class SyncUiState(val list: List<SyncStateData>) : OfflineDashboardUiState()
}

data class SyncStateData(
    var count: Int = 0,
    val name: Int = -1,
    val type: Type,
    var errorMsg: String? = null,
)
