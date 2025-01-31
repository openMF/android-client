/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.newIndividualCollectionSheet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.GetIndividualCollectionSheetUseCase
import com.mifos.core.domain.useCases.GetOfficeListUseCase
import com.mifos.core.domain.useCases.GetStaffInOfficeUseCase
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewIndividualCollectionSheetViewModel @Inject constructor(
    private val getOfficeListUseCase: GetOfficeListUseCase,
    private val getStaffInOfficeUseCase: GetStaffInOfficeUseCase,
    private val getIndividualCollectionSheetUseCase: GetIndividualCollectionSheetUseCase,
) : ViewModel() {

    private val _newIndividualCollectionSheetUiState =
        MutableStateFlow(NewIndividualCollectionSheetUiState())

    val newIndividualCollectionSheetUiState =
        _newIndividualCollectionSheetUiState.asStateFlow()

    init {
        getOfficeList()
    }

    private fun getOfficeList() = viewModelScope.launch(Dispatchers.IO) {
        getOfficeListUseCase().collect { result ->
            when (result) {
                is Resource.Error -> {
                    _newIndividualCollectionSheetUiState.value =
                        _newIndividualCollectionSheetUiState.value.copy(
                            error = result.message,
                        )
                }

                is Resource.Loading -> {
                    _newIndividualCollectionSheetUiState.value =
                        _newIndividualCollectionSheetUiState.value.copy(
                            isLoading = true,
                        )
                }

                is Resource.Success -> {
                    _newIndividualCollectionSheetUiState.value =
                        _newIndividualCollectionSheetUiState.value.copy(
                            isLoading = false,
                            officeList = result.data ?: emptyList(),
                        )
                }
            }
        }
    }

    fun getStaffList(officeId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getStaffInOfficeUseCase(officeId).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _newIndividualCollectionSheetUiState.value =
                        _newIndividualCollectionSheetUiState.value.copy(
                            error = result.message,
                        )
                }

                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    _newIndividualCollectionSheetUiState.value =
                        _newIndividualCollectionSheetUiState.value.copy(
                            staffList = result.data ?: emptyList(),
                        )
                }
            }
        }
    }

    fun getIndividualCollectionSheet(requestCollectionSheetPayload: RequestCollectionSheetPayload) =
        viewModelScope.launch(Dispatchers.IO) {
            getIndividualCollectionSheetUseCase(requestCollectionSheetPayload).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _newIndividualCollectionSheetUiState.value =
                            _newIndividualCollectionSheetUiState.value.copy(
                                error = result.message,
                            )
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        _newIndividualCollectionSheetUiState.value =
                            _newIndividualCollectionSheetUiState.value.copy(
                                individualCollectionSheet = result.data
                                    ?: IndividualCollectionSheet(),
                            )
                    }
                }
            }
        }
}
