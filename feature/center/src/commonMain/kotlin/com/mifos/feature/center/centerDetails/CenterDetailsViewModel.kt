/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.centerDetails

import androidclient.feature.center.generated.resources.Res
import androidclient.feature.center.generated.resources.feature_center_error_loading_centers
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.GetCenterDetailsUseCase
import com.mifos.core.model.objects.groups.CenterInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CenterDetailsViewModel(
    private val getCenterDetailsUseCase: GetCenterDetailsUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val centerId = savedStateHandle.getStateFlow(key = Constants.CENTER_ID, initialValue = 0)

    private val _centerDetailsUiState =
        MutableStateFlow<CenterDetailsUiState>(CenterDetailsUiState.Loading)
    val centerDetailsUiState = _centerDetailsUiState.asStateFlow()

    fun loadClientDetails(centerId: Int) = viewModelScope.launch {
        getCenterDetailsUseCase(centerId, false).collect { result ->
            when (result) {
                is DataState.Error ->
                    _centerDetailsUiState.value =
                        CenterDetailsUiState.Error(Res.string.feature_center_error_loading_centers)

                is DataState.Loading -> _centerDetailsUiState.value = CenterDetailsUiState.Loading

                is DataState.Success -> {
                    result.data.let {
                        _centerDetailsUiState.value = CenterDetailsUiState.CenterDetails(
                            it.first,
                            if (it.second.isNotEmpty()) it.second[0] else CenterInfo(),
                        )
                    }
                }
            }
        }
    }
}
