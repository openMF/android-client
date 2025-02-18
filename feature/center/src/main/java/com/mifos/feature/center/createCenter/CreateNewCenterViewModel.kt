/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.createCenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.CreateNewCenterRepository
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.feature.center.R
import com.mifos.room.entities.center.CenterPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNewCenterViewModel @Inject constructor(
//    private val getOfficeListUseCase: GetOfficeListUseCase,
//    private val createNewCenterUseCase: CreateNewCenterUseCase,
    private val repository: CreateNewCenterRepository,
    private val collectionSheetRepo: NewIndividualCollectionSheetRepository,
) : ViewModel() {

    private val _createNewCenterUiState =
        MutableStateFlow<CreateNewCenterUiState>(CreateNewCenterUiState.Loading)
    val createNewCenterUiState = _createNewCenterUiState.asStateFlow()

    fun loadOffices() {
        viewModelScope.launch {
            _createNewCenterUiState.value =
                CreateNewCenterUiState.Loading

            collectionSheetRepo.offices()
                .catch {
                    _createNewCenterUiState.value =
                        CreateNewCenterUiState.Error(R.string.feature_center_failed_to_load_offices)
                }.collect {
                    _createNewCenterUiState.value =
                        CreateNewCenterUiState.Offices(it)
                }
        }
    }

    fun createNewCenter(centerPayload: CenterPayload) {
        viewModelScope.launch {
            _createNewCenterUiState.value = CreateNewCenterUiState.Loading
            try {
                repository.createCenter(centerPayload)
                _createNewCenterUiState.value =
                    CreateNewCenterUiState.CenterCreatedSuccessfully
            } catch (e: Exception) {
                _createNewCenterUiState.value =
                    CreateNewCenterUiState.Error(R.string.feature_center_failed_to_create_center)
            }
        }
    }
}
