/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.newIndividualCollectionSheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.domain.useCases.GetIndividualCollectionSheetUseCase
import com.mifos.core.domain.useCases.GetStaffInOfficeUseCase
import com.mifos.core.network.model.RequestCollectionSheetPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NewIndividualCollectionSheetViewModel(
    private val newIndividualCollectionSheetRepository: NewIndividualCollectionSheetRepository,
    private val getStaffInOfficeUseCase: GetStaffInOfficeUseCase,
    private val getIndividualCollectionSheetUseCase: GetIndividualCollectionSheetUseCase,
) : ViewModel() {

    private val uiStateInternal = MutableStateFlow(NewIndividualCollectionSheetUiState())
    val newIndividualCollectionSheetUiState = uiStateInternal.asStateFlow()

    init {
        getOfficeList()
    }

    private fun getOfficeList() {
        updateUiState { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            newIndividualCollectionSheetRepository.offices()
                .catch { error ->
                    updateUiState { it.copy(isLoading = false, error = error.message) }
                }
                .collect { result ->
                    when (result) {
                        is DataState.Loading -> Unit
                        is DataState.Error -> updateUiState {
                            it.copy(isLoading = false, error = result.message)
                        }
                        is DataState.Success -> updateUiState {
                            it.copy(isLoading = false, officeList = result.data)
                        }
                    }
                }
        }
    }

    fun getStaffList(officeId: Int) = viewModelScope.launch {
        getStaffInOfficeUseCase(officeId).collect { result ->
            when (result) {
                is DataState.Loading -> Unit
                is DataState.Error -> updateUiState {
                    it.copy(error = result.message)
                }
                is DataState.Success -> updateUiState {
                    it.copy(staffList = result.data)
                }
            }
        }
    }

    fun getIndividualCollectionSheet(payload: RequestCollectionSheetPayload) = viewModelScope.launch {
        getIndividualCollectionSheetUseCase(payload).collect { result ->
            when (result) {
                is DataState.Loading -> Unit
                is DataState.Error -> updateUiState {
                    it.copy(error = result.message)
                }
                is DataState.Success -> updateUiState {
                    it.copy(individualCollectionSheet = result.data)
                }
            }
        }
    }

    private inline fun updateUiState(update: (NewIndividualCollectionSheetUiState) -> NewIndividualCollectionSheetUiState) {
        uiStateInternal.value = update(uiStateInternal.value)
    }
}
