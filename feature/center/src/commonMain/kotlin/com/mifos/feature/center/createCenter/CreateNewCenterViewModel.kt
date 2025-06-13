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

import androidclient.feature.center.generated.resources.Res
import androidclient.feature.center.generated.resources.feature_center_failed_to_create_center
import androidclient.feature.center.generated.resources.feature_center_failed_to_load_offices
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.CreateNewCenterRepository
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.room.entities.center.CenterPayloadEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateNewCenterViewModel(
    private val repository: CreateNewCenterRepository,
    private val collectionSheetRepo: NewIndividualCollectionSheetRepository,
) : ViewModel() {

    private val _createNewCenterUiState =
        MutableStateFlow<CreateNewCenterUiState>(CreateNewCenterUiState.Loading)
    val createNewCenterUiState = _createNewCenterUiState.asStateFlow()

    fun loadOffices() {
        viewModelScope.launch {
            collectionSheetRepo.offices()
                .collect {
                    when (it) {
                        is DataState.Error -> {
                            _createNewCenterUiState.value =
                                CreateNewCenterUiState.Error(Res.string.feature_center_failed_to_load_offices)
                        }
                        DataState.Loading -> {
                            _createNewCenterUiState.value =
                                CreateNewCenterUiState.Loading
                        }
                        is DataState.Success -> {
                            _createNewCenterUiState.value =
                                CreateNewCenterUiState.Offices(it.data)
                        }
                    }
                }
        }
    }

    fun createNewCenter(centerPayload: CenterPayloadEntity) {
        viewModelScope.launch {
            _createNewCenterUiState.value = CreateNewCenterUiState.Loading
            try {
                repository.createCenter(centerPayload)
                _createNewCenterUiState.value =
                    CreateNewCenterUiState.CenterCreatedSuccessfully
            } catch (e: Exception) {
                _createNewCenterUiState.value =
                    CreateNewCenterUiState.Error(Res.string.feature_center_failed_to_create_center)
            }
        }
    }
}
