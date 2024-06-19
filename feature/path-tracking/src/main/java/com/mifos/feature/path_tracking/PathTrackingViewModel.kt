package com.mifos.feature.path_tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.GetUserPathTrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PathTrackingViewModel @Inject constructor(
    private val getUserPathTrackingUseCase: GetUserPathTrackingUseCase
) : ViewModel() {

    private val _pathTrackingUiState =
        MutableStateFlow<PathTrackingUiState>(PathTrackingUiState.Loading)
    val pathTrackingUiState = _pathTrackingUiState.asStateFlow()


    fun loadPathTracking(userId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getUserPathTrackingUseCase(userId).collect { result ->
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

}