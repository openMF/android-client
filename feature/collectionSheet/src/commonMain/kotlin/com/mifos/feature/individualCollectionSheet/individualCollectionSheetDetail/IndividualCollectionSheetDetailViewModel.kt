/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetail

import androidx.lifecycle.ViewModel
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class IndividualCollectionSheetDetailViewModel : ViewModel() {

    private val uiStateInternal = MutableStateFlow(IndividualCollectionSheetDetailUiState())
    val individualCollectionSheetDetailUiState = uiStateInternal.asStateFlow()

    fun setCollectionSheetData(repaymentDate: String, collectionSheet: IndividualCollectionSheet) {
        updateUiState {
            it.copy(
                repaymentDate = repaymentDate,
                collectionSheet = collectionSheet,
                isLoading = false,
            )
        }
    }

    private inline fun updateUiState(update: (IndividualCollectionSheetDetailUiState) -> IndividualCollectionSheetDetailUiState) {
        uiStateInternal.value = update(uiStateInternal.value)
    }
}
