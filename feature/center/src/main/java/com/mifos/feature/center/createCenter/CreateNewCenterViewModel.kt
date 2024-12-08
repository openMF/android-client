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
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.CenterPayload
import com.mifos.core.domain.useCases.CreateNewCenterUseCase
import com.mifos.core.domain.useCases.GetOfficeListUseCase
import com.mifos.feature.center.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNewCenterViewModel @Inject constructor(
    private val getOfficeListUseCase: GetOfficeListUseCase,
    private val createNewCenterUseCase: CreateNewCenterUseCase,
) : ViewModel() {

    private val _createNewCenterUiState =
        MutableStateFlow<CreateNewCenterUiState>(CreateNewCenterUiState.Loading)
    val createNewCenterUiState = _createNewCenterUiState.asStateFlow()

    fun loadOffices() = viewModelScope.launch(Dispatchers.IO) {
        getOfficeListUseCase().collect { result ->
            when (result) {
                is Resource.Error ->
                    _createNewCenterUiState.value =
                        CreateNewCenterUiState.Error(R.string.feature_center_failed_to_load_offices)

                is Resource.Loading ->
                    _createNewCenterUiState.value =
                        CreateNewCenterUiState.Loading

                is Resource.Success ->
                    _createNewCenterUiState.value =
                        CreateNewCenterUiState.Offices(result.data ?: emptyList())
            }
        }
    }

    fun createNewCenter(centerPayload: CenterPayload) = viewModelScope.launch(Dispatchers.IO) {
        createNewCenterUseCase(centerPayload).collect { result ->
            when (result) {
                is Resource.Error ->
                    _createNewCenterUiState.value =
                        CreateNewCenterUiState.Error(R.string.feature_center_failed_to_create_center)

                is Resource.Loading ->
                    _createNewCenterUiState.value =
                        CreateNewCenterUiState.Loading

                is Resource.Success ->
                    _createNewCenterUiState.value =
                        CreateNewCenterUiState.CenterCreatedSuccessfully
            }
        }
    }
}
