/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.path.tracking

import androidclient.feature.path_tracking.generated.resources.Res
import androidclient.feature.path_tracking.generated.resources.feature_path_tracking_failed_to_load_path_tracking
import androidclient.feature.path_tracking.generated.resources.feature_path_tracking_no_path_tracking_found
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.domain.useCases.GetUserPathTrackingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PathTrackingViewModel(
    private val getUserPathTrackingUseCase: GetUserPathTrackingUseCase,
    private val prefManager: UserPreferencesRepository,
) : ViewModel() {

    private val _pathTrackingUiState =
        MutableStateFlow<PathTrackingUiState>(PathTrackingUiState.Loading)
    val pathTrackingUiState = _pathTrackingUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val userStatus: StateFlow<Boolean?> = prefManager.userInfo
        .map { it.userStatus }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    fun refreshCenterList() {
        _isRefreshing.value = true
        loadPathTracking()
        _isRefreshing.value = false
    }

    fun loadPathTracking() = viewModelScope.launch {
        val officeId = prefManager.userData.firstOrNull()?.officeId
        if (officeId != null) {
            getUserPathTrackingUseCase(officeId.toInt()).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _pathTrackingUiState.value =
                            PathTrackingUiState.Error(Res.string.feature_path_tracking_failed_to_load_path_tracking)

                    is DataState.Loading -> _pathTrackingUiState.value = PathTrackingUiState.Loading

                    is DataState.Success ->
                        result.data.let { pathTracking ->
                            _pathTrackingUiState.value =
                                if (pathTracking.isEmpty()) {
                                    PathTrackingUiState.Error(Res.string.feature_path_tracking_no_path_tracking_found)
                                } else {
                                    PathTrackingUiState.PathTracking(
                                        pathTracking,
                                    )
                                }
                        }
                }
            }
        } else {
            _pathTrackingUiState.value =
                PathTrackingUiState.Error(Res.string.feature_path_tracking_no_path_tracking_found)
        }
    }

    fun updateUserStatus(status: Boolean) = viewModelScope.launch {
        prefManager.updateUserStatus(status)
    }
}
