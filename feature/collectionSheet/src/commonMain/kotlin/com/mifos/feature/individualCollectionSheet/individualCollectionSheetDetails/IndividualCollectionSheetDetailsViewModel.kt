/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.individualCollectionSheetDetails

import androidclient.feature.collectionsheet.generated.resources.Res
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_failed_to_save_collection_sheet
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.SaveIndividualCollectionSheetUseCase
import com.mifos.core.model.objects.collectionsheets.LoanAndClientName
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.room.entities.collectionsheet.ClientCollectionSheet
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString

class IndividualCollectionSheetDetailsViewModel(
    private val saveIndividualCollectionSheetUseCase: SaveIndividualCollectionSheetUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val arg = savedStateHandle.getStateFlow(Constants.INDIVIDUAL_SHEET, "")

    val sheet: IndividualCollectionSheet = runCatching {
        Json.decodeFromString<IndividualCollectionSheet>(arg.value)
    }.getOrElse {
        IndividualCollectionSheet()
    }

    @Suppress("ktlint:standard:property-naming")
    private val uiStateInternal = MutableStateFlow<IndividualCollectionSheetDetailsUiState>(
        IndividualCollectionSheetDetailsUiState.Empty,
    )
    val individualCollectionSheetDetailsUiState = uiStateInternal.asStateFlow()

    fun submitIndividualCollectionSheet(payload: IndividualCollectionSheetPayload) = viewModelScope.launch {
        val errorMsg = getString(Res.string.feature_collection_sheet_failed_to_save_collection_sheet)
        saveIndividualCollectionSheetUseCase(payload).collect { result ->
            uiStateInternal.value = when (result) {
                is DataState.Loading -> IndividualCollectionSheetDetailsUiState.Loading
                is DataState.Success -> IndividualCollectionSheetDetailsUiState.SavedSuccessfully
                is DataState.Error -> IndividualCollectionSheetDetailsUiState.Error(errorMsg)
            }
        }
    }

    fun filterLoanAndClientNames(clientSheets: List<ClientCollectionSheet>): List<LoanAndClientName> {
        return clientSheets.flatMap { client ->
            client.loans.orEmpty().map { loan ->
                LoanAndClientName(
                    loan = loan,
                    clientName = client.clientName,
                    id = client.clientId,
                )
            }
        }
    }
}
