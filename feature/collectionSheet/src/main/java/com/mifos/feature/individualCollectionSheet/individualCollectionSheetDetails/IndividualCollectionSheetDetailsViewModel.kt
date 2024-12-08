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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.SaveIndividualCollectionSheetUseCase
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.ClientCollectionSheet
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.collectionsheet.LoanAndClientName
import com.mifos.feature.collection_sheet.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rx.Observable
import javax.inject.Inject

@HiltViewModel
class IndividualCollectionSheetDetailsViewModel @Inject constructor(
    private val saveIndividualCollectionSheetUseCase: SaveIndividualCollectionSheetUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val arg = savedStateHandle.getStateFlow(key = Constants.INDIVIDUAL_SHEET, initialValue = "")
    val sheet: IndividualCollectionSheet = Gson().fromJson(arg.value, IndividualCollectionSheet::class.java)

    private val _individualCollectionSheetDetailsUiState =
        MutableStateFlow<IndividualCollectionSheetDetailsUiState>(
            IndividualCollectionSheetDetailsUiState.Empty,
        )
    val individualCollectionSheetDetailsUiState =
        _individualCollectionSheetDetailsUiState.asStateFlow()

    fun submitIndividualCollectionSheet(payload: IndividualCollectionSheetPayload) =
        viewModelScope.launch(Dispatchers.IO) {
            saveIndividualCollectionSheetUseCase(payload).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _individualCollectionSheetDetailsUiState.value =
                            IndividualCollectionSheetDetailsUiState.Error(R.string.feature_collection_sheet_failed_to_save_collection_sheet)

                    is Resource.Loading ->
                        _individualCollectionSheetDetailsUiState.value =
                            IndividualCollectionSheetDetailsUiState.Loading

                    is Resource.Success ->
                        _individualCollectionSheetDetailsUiState.value =
                            IndividualCollectionSheetDetailsUiState.SavedSuccessfully
                }
            }
        }

    fun filterLoanAndClientNames(clientCollectionSheets: List<ClientCollectionSheet>): List<LoanAndClientName> {
        val loansAndClientNames: MutableList<LoanAndClientName> = ArrayList()
        Observable.from(clientCollectionSheets)
            .subscribe { clientCollectionSheet ->
                clientCollectionSheet.loans?.let {
                    for (loanCollectionSheet in it) {
                        loansAndClientNames.add(
                            LoanAndClientName(
                                loanCollectionSheet,
                                clientCollectionSheet.clientName,
                                clientCollectionSheet.clientId,
                            ),
                        )
                    }
                }
            }
        return loansAndClientNames
    }
}
