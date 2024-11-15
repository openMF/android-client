/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.generateCollectionSheet

/**
 * Created by Aditya Gupta on 12/08/23.
 */
sealed class GenerateCollectionSheetUiState {

    data object Loading : GenerateCollectionSheetUiState()

    data class Error(val message: Int) : GenerateCollectionSheetUiState()

    data object Success : GenerateCollectionSheetUiState()

    data object CollectionSheetSuccess : GenerateCollectionSheetUiState()

    data object ProductiveSheetSuccess : GenerateCollectionSheetUiState()
}
