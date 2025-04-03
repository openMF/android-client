/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.pathTracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.domain.useCases.GetUserPathTrackingUseCase
import com.mifos.feature.path.tracking.R
import kotlinx.coroutines.Dispatchers
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

    fun loadPathTracking() = viewModelScope.launch(Dispatchers.IO) {
        val userId = prefManager.userData.firstOrNull()?.userId
        if (userId != null) {
            getUserPathTrackingUseCase(userId.toInt()).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _pathTrackingUiState.value =
                            PathTrackingUiState.Error(R.string.feature_path_tracking_failed_to_load_path_tracking)

                    is Resource.Loading -> _pathTrackingUiState.value = PathTrackingUiState.Loading

                    is Resource.Success ->
                        result.data?.let { pathTracking ->
                            _pathTrackingUiState.value =
                                if (pathTracking.isEmpty()) {
                                    PathTrackingUiState.Error(R.string.feature_path_tracking_no_path_tracking_found)
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
                PathTrackingUiState.Error(R.string.feature_path_tracking_no_path_tracking_found)
        }
    }

    fun updateUserStatus(status: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        prefManager.updateUserStatus(status)
    }
}
