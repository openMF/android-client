package com.mifos.feature.path_tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.datastore.PrefManager
import com.mifos.core.domain.use_cases.GetUserPathTrackingUseCase
import com.mifos.feature.path.tracking.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PathTrackingViewModel @Inject constructor(
    private val getUserPathTrackingUseCase: GetUserPathTrackingUseCase,
    private val prefManager: PrefManager
) : ViewModel() {

    private val _pathTrackingUiState =
        MutableStateFlow<PathTrackingUiState>(PathTrackingUiState.Loading)
    val pathTrackingUiState = _pathTrackingUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _userStatus = MutableStateFlow(prefManager.userStatus)
    val userStatus = _userStatus.asStateFlow()

    fun refreshCenterList() {
        _isRefreshing.value = true
        loadPathTracking()
        _isRefreshing.value = false
    }


    fun loadPathTracking() = viewModelScope.launch(Dispatchers.IO) {
        getUserPathTrackingUseCase(prefManager.getUserId()).collect { result ->
            when (result) {
                is Resource.Error -> _pathTrackingUiState.value =
                    PathTrackingUiState.Error(R.string.feature_path_tracking_failed_to_load_path_tracking)

                is Resource.Loading -> _pathTrackingUiState.value = PathTrackingUiState.Loading

                is Resource.Success ->
                    result.data?.let { pathTracking ->
                        _pathTrackingUiState.value =
                            if (pathTracking.isEmpty()) PathTrackingUiState.Error(R.string.feature_path_tracking_no_path_tracking_found) else PathTrackingUiState.PathTracking(
                                pathTracking
                            )
                    }
            }

        }
    }

    fun updateUserStatus(status: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        prefManager.userStatus = status
        _userStatus.value = status
    }

}